/**
 * WARNING: THIS FUNCTION EXISTS FOR DEMO PORPUSES AND SHOULD NOT BE USED IN PRODUCTION.
 */
exports = async function(arg){
    const clientFilesCollection = context.services
        .get("mongodb-atlas")
        .db("__realm_sync_" + context.app.id)
        .collection("clientfiles");

    try {
        let result = await clientFilesCollection.deleteMany(
            {
                ownerId: context.user.id,
            }
        );
        return result.deletedCount;
    } catch (e) {
        console.error(`Failed to delete client file for user: ${context.user.id}`);
        throw e
    }
};