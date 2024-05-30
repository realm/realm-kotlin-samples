# Dynamic data

With the newly added ability to store collections in mixed properties, you can now store and synchronized 
data without pre-known schema. 

This app just holds a single data class that mixes strictly typed properties and a single mixed property
that will as entry for point for the, potentially deeply nested, dynamic data. 

The Kotlin UI shows each entity and associated 'configuration' mixed property in a tree view. There is 
currently no update options in the UI, so data has to be added thought the Atlas UI in the 
"Data Service" section under _Database_'->'_Collections_`->`_dynamic-data_`->`_DynamicDataEntity_.

Updates will be reflected in the Kotlin UI. 
