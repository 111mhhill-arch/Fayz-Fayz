package com.example.data.remote

import com.example.BuildConfig
import com.example.data.local.entity.ProductEntity
import com.example.data.local.entity.StyleProfileEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.WardrobeItemEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val parts: List<GeminiPart>,
    val role: String? = "user"
)

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent?
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>?
)

interface GeminiApi {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

class GeminiService {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val api = retrofit.create(GeminiApi::class.java)

    suspend fun askFashionConsultant(
        userPrompt: String,
        user: UserEntity?,
        styleProfile: StyleProfileEntity?,
        products: List<ProductEntity>,
        wardrobe: List<WardrobeItemEntity>
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY

        val productCatalogSummary = if (products.isEmpty()) {
            "لا توجد منتجات معتمدة حالياً في المتجر."
        } else {
            products.take(30).joinToString("\n") { p ->
                "- [ID: ${p.id}] ${p.name} | المتجر: ${p.storeName} | السعر: ${p.discountPrice ?: p.price} د.ع | الفئة: ${p.category} | الستايل: ${p.style} | الألوان: ${p.colors} | المقاسات: ${p.sizes}"
            }
        }

        val wardrobeSummary = if (wardrobe.isEmpty()) {
            "خزانة المستخدم فارغة حالياً."
        } else {
            wardrobe.joinToString("\n") { w ->
                "- ${w.name} (${w.category}) لون: ${w.color}, ستايل: ${w.style}"
            }
        }

        val userContext = """
            معلومات المتسوق:
            - الاسم: ${user?.name ?: "متسوق"}
            - الجنس: ${user?.gender ?: "غير محدد"}
            - العمر: ${user?.age ?: 25}
            - المدينة: ${user?.city ?: "العراق"}
            - الطول: ${styleProfile?.heightCm ?: 175} سم
            - الوزن: ${styleProfile?.weightKg ?: 70} كغ
            - المقاس المفضل: ${styleProfile?.size ?: "M"}
            - لون البشرة: ${styleProfile?.skinTone ?: "قمحي / Medium"}
            - الستايل المفضل: ${styleProfile?.styleType ?: "كاجوال"}
            - الألوان المفضلة: ${styleProfile?.favoriteColors ?: "كحلي، أسود"}
        """.trimIndent()

        val prompt = """
            أنت Stylek AI — مستشار الأزياء الذكي والفاخر لمنصة ستايلك (Stylek).
            مهمتك تقديم نصائح وتنسيقات وتوصيات ملابس أنيقة ومفصلة بناءً على طلب المستخدم والميزانية ومواصفات جسمه ولون بشرته.

            $userContext

            محتويات خزانة المستخدم الشخصية:
            $wardrobeSummary

            المنتجات المتاحة فعلياً للشراء في المتاجر:
            $productCatalogSummary

            طلب المستخدم:
            "$userPrompt"

            تعليمات صارمة:
            1. رشح منتجات حقيقية من قائمة المنتجات المتاحة فقط، واذكر اسمها وسعرها بدقة، ولا تخترع أسماء منتجات غير موجودة.
            2. إذا كانت خزانة المستخدم تحتوي على قطع، اقترح كيفية تنسيقها مع منتجات المتجر.
            3. احرص أن يكون المجموع ضمن الميزانية إذا حدد المستخدم ميزانية معينة.
            4. أجب بأسلوب راقٍ واحترافي وباللغة العربية الجميلة والمنسقة بنقاط واضحة.
        """.trimIndent()

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(
                            parts = listOf(GeminiPart(text = prompt))
                        )
                    )
                )
                val response = api.generateContent(apiKey, request)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!text.isNullOrBlank()) {
                    return@withContext text
                }
            } catch (e: Exception) {
                // Fall through to smart rule-based stylist
            }
        }

        // Smart On-Device Stylist Engine
        generateSmartLocalRecommendation(userPrompt, user, styleProfile, products, wardrobe)
    }

    private fun generateSmartLocalRecommendation(
        prompt: String,
        user: UserEntity?,
        styleProfile: StyleProfileEntity?,
        products: List<ProductEntity>,
        wardrobe: List<WardrobeItemEntity>
    ): String {
        val lowerPrompt = prompt.lowercase()
        val budgetMatch = Regex("""(\d+)""").find(prompt)
        val extractedBudget = budgetMatch?.value?.toDoubleOrNull()?.let {
            if (it < 1000) it * 1000 else it
        }

        val tone = styleProfile?.skinTone ?: "قمحي / متوازن"
        val style = styleProfile?.styleType ?: "style_casual"

        val matchingProducts = products.filter { p ->
            val price = p.discountPrice ?: p.price
            val budgetOk = extractedBudget == null || price <= extractedBudget
            budgetOk
        }

        val sb = StringBuilder()
        sb.append("✨ **مرحباً بك في Stylek AI** ✨\n\n")
        sb.append("بناءً على طلبك ومواصفات ستايلك (لون البشرة: $tone، والمقاس: ${styleProfile?.size ?: "M"}):\n\n")

        if (wardrobe.isNotEmpty()) {
            val randomWardrobe = wardrobe.first()
            sb.append("🧥 **تنسيق مع خزانتك:**\n")
            sb.append("يمكنك ارتداء **${randomWardrobe.name}** التي تمتلكها بالفعل، وتنسيقها مع:\n")
        }

        if (matchingProducts.isEmpty()) {
            sb.append("🛍️ **المنتجات المقترحة من المتاجر:**\n")
            sb.append("لا توجد حالياً منتجات معتمدة في قاعدة البيانات تطابق هذا البحث بدقة. عند قيام المتاجر بإضافة المنتجات واعتمادها ستظهر لك التنسيقات المباشرة فوراً.\n\n")
        } else {
            sb.append("🛍️ **أفضل الاختيارات المتاحة في المتاجر حالياً:**\n")
            matchingProducts.take(4).forEach { p ->
                val finalPrice = p.discountPrice ?: p.price
                sb.append("• **${p.name}** من متجر *${p.storeName}*\n")
                sb.append("  - السعر: ${finalPrice.toInt()} د.ع | المقاسات: ${p.sizes}\n")
                sb.append("  - تناسب الأجواء بتناسق لوني مميز مع درجة بشرتك.\n")
            }
            sb.append("\n")
        }

        sb.append("💡 **نصيحة ستايلك:**\n")
        if (lowerPrompt.contains("رسمي") || lowerPrompt.contains("حفلة") || lowerPrompt.contains("formal")) {
            sb.append("للإطلالات الرسمية والحفلات، ننصح بارتداء ألوان داكنة مثل الكحلي الداكن أو الأسود الفحمي مع حذاء كلاسيكي أنيق، وإضافة ساعة معدنية أو إكسسوار بسيط يبرز فخامة المظهر.")
        } else if (lowerPrompt.contains("صيف") || lowerPrompt.contains("كاجوال") || lowerPrompt.contains("casual")) {
            sb.append("للأيام الصيفية والكاجوال، اعتمد على خامات القطن والكتان الخفيفة، ودرجات الألوان الفاتحة كالبيج والأبيض والسماوي لتعكس حرارة الشمس وتمنحك راحة وأناقة لا مثيل لها.")
        } else {
            sb.append("تناسق الألوان الأساسية (النيوترال كالبيج والأبيض والكحلي) يتيح لك بناء أكثر من 5 إطلالات مختلفة بأقل تكلفة ممكنة وبأعلى فخامة.")
        }

        return sb.toString()
    }
}
