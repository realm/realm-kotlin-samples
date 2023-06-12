const crypto = require('crypto');

// This function initializes an empty user password protected keystore and defines the algorithm that
// would be used to encrypt any fields.
exports = async function (user) {
    const salt = crypto.randomBytes(16);

    const customUserDataCollection = context.services
        .get("mongodb-atlas")
        .db("field_encryption")
        .collection("custom_data");

    try {
        await customUserDataCollection.insertOne({
            // Binds this custom data to the user.
            owner_id: user.id,
            // Defines the field level encryption algorithm.
            field_encryption_cipher_spec: {
                algorithm: "AES",
                block: "CBC",
                padding: "PKCS7Padding",
                key_length: 128
            },
            // User keystore
            key_store: {
                // Password based key specs
                encryption_key_spec: {
                    algorithm: "PBKDF2WithHmacSHA256",
                    salt: BSON.Binary.fromHex(salt.toString('hex')),
                    iterations_count: 100000,
                    key_length: 128,
                },
                // Encryption cipher spec to secure keystore contents.
                cipher_spec: {
                    algorithm: "AES",
                    block: "CBC",
                    padding: "PKCS7Padding",
                    key_length: 128
                },
                // Secured contents, being null would tell the client that it has to initialize them.
                secure_contents: null,
                // Null as no contents exists yet.
                key_hash: null,
            }
        });
    } catch (e) {
        console.error(`Failed to create custom user data document for user: ${user.id}`);
        throw e
    }
};
