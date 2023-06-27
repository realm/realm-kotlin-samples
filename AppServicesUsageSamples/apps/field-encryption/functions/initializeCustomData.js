// Executing this function would generate a new encryption key and store it in the user custom data
exports = async function (user) {
    const customUserDataCollection = context.services
        .get("mongodb-atlas")
        .db("field_encryption")
        .collection("custom_data");

    try {
        await customUserDataCollection.insertOne({
            // Bind this user custom data to the new user
            owner_id: user.id,
            // Algorithm spec for field level encryption
            fle_cipher_spec: {
                algorithm: "AES",
                block: "CBC",
                padding: "PKCS7Padding",
                key_length: 128
            },
            // Uninitialized BKS keystore
            key_store: null
        });
    } catch (e) {
        console.error(`Failed to create custom user data document for user: ${user.id}`);
        throw e
    }
};
