exports = async function (arg) {
    const customUserDataCollection = context.services
        .get("mongodb-atlas")
        .db("property_encryption")
        .collection("custom_data");

    try {
        await customUserDataCollection.updateOne(
            {
                // Update the users custom data
                owner_id: context.user.id
            },
            {
                $set: { key_store: arg }
            }
        );
        return true;
    } catch (e) {
        console.error(`Failed to create custom user data document for user: ${context.user.id}`);
        throw e
    }
};
