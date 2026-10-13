const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read products", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("products").get());
});

test("Authenticated user: can create and read their own user profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).set({
    userId: ALICE_UID,
    role: "USER",
    name: "Alice",
    email: "alice@example.com"
  }));
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: cannot impersonate or write another user profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).set({
    userId: BOB_UID,
    role: "USER",
    name: "Bob",
    email: "bob@example.com"
  }));
});

test("Merchant product creation: requires valid schema", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("products").doc("prod1").set({
    productId: "prod1",
    userId: ALICE_UID,
    merchantId: ALICE_UID,
    name: "Classic Suit",
    category: "cat_men",
    price: 99.0,
    status: "PENDING"
  }));
});

test("Approved products: can be read by any signed in user", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("products").doc("prod_approved").set({
      productId: "prod_approved",
      userId: BOB_UID,
      merchantId: BOB_UID,
      name: "Approved Shirt",
      price: 45.0,
      status: "APPROVED"
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("products").doc("prod_approved").get());
});
