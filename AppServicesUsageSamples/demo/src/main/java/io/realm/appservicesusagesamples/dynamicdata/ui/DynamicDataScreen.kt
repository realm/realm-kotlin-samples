package io.realm.appservicesusagesamples.dynamicdata.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.bonsai.core.Bonsai
import cafe.adriel.bonsai.core.node.Branch
import cafe.adriel.bonsai.core.node.Leaf
import cafe.adriel.bonsai.core.tree.Tree
import cafe.adriel.bonsai.core.tree.TreeScope
import io.realm.appservicesusagesamples.dynamicdata.models.DynamicDataEntity
import io.realm.kotlin.types.RealmAny
import io.realm.kotlin.types.RealmDictionary
import io.realm.kotlin.types.RealmList

@Composable
fun DynamicDataScreen(
    viewModel: DynamicDataViewModel,
) {
    val entities: List<DynamicDataEntity> by viewModel.instances.observeAsState(emptyList())

    Column {
        entities.forEach { item ->
            Column {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                ) {
                    Text(
                        text = "Name: ${item.name}",
                        modifier =
                        Modifier
                            .padding(start = 4.dp)
                            .align(Alignment.CenterHorizontally),
                    )
                    Text(
                        text = "Id: ${item.id.toHexString()}",
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .align(Alignment.CenterHorizontally),
                    )
                    Divider()
                    item.configuration?.let {
                        Bonsai(tree = Tree<RealmAny> { RealmAnyNode("Configuration", it) })
                    }
                }
            }
        }
    }
}

@Composable
fun TreeScope.RealmAnyNode(
    key: String?,
    element: RealmAny?,
) {
    when (element?.type) {
        RealmAny.Type.LIST -> RealmAnyListNode("$key: ${element.type}", element.asList())
        RealmAny.Type.DICTIONARY -> RealmAnyDictionaryNode(
            "$key: ${element.type}",
            element.asDictionary()
        )

        else -> RealmAnyPrimitiveNode("$key", element)
    }
}

@Composable
fun TreeScope.RealmAnyListNode(property: String?, element: RealmList<RealmAny?>) {
    Branch(content = element, name = "$property") {
        element.forEachIndexed { index, element -> RealmAnyNode(key = "$index", element = element) }
    }
}

@Composable
fun TreeScope.RealmAnyDictionaryNode(property: String?, element: RealmDictionary<RealmAny?>) {
    Branch(content = element, name = "$property") {
        element.forEach { (key, value) -> RealmAnyNode(key = "$key", element = value) }
    }
}

@Composable
fun TreeScope.RealmAnyPrimitiveNode(key: String?, element: RealmAny?) {
    Leaf(content = element, name = "$key: $element")
}
