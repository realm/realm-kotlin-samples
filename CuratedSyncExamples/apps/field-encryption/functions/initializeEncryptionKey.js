const crypto = require('crypto');

// Executing this function would generate a new encryption key and store it in the user custom data
exports = async function (user) {
    const salt = crypto.randomBytes(16);

    const customUserDataCollection = context.services
        .get("mongodb-atlas")
        .db("field_encryption")
        .collection("custom_data");

    try {
        await customUserDataCollection.insertOne({
            // Save the user's account ID to your configured user_id_field
            owner_id: user.id,
            // Store any other user data you want
            field_encryption_cipher_spec: {
                algorithm: "AES",
                block: "CBC",
                padding: "PKCS7Padding",
                key_length: 128
            },
            key_store: {
                encryption_key_spec: {
                    algorithm: "PBKDF2WithHmacSHA256",
                    salt: BSON.Binary.fromHex(salt.toString('hex')),
                    iterations_count: 100000,
                    key_length: 128,
                },
                cipher_spec: {
                    algorithm: "AES",
                    block: "CBC",
                    padding: "PKCS7Padding",
                    key_length: 128
                },
                secure_contents: null,
                key_hash: null,
            }
        });
    } catch (e) {
        console.error(`Failed to create custom user data document for user: ${user.id}`);
        throw e
    }
};