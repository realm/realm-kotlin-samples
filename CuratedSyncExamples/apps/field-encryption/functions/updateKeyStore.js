exports = async function (arg) {
    const customUserDataCollection = context.services
        .get("mongodb-atlas")
        .db("field_encryption")
        .collection("custom_data");

    try {
        console.log(context.user.id)
        await customUserDataCollection.updateOne(
            {
                // Save the user's account ID to your configured user_id_field
                owner_id: context.user.id
            },
            {
                $set: {key_store: arg}
            }
        );
        return true;
    } catch (e) {
        console.error(`Failed to create custom user data document for user: ${context.user.id}`);
        throw e
    }
};