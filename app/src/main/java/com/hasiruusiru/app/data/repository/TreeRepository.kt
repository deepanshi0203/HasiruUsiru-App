package com.hasiruusiru.app.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hasiruusiru.app.data.model.Species
import com.hasiruusiru.app.data.model.Tree
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

class TreeRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("hasiru_usiru_db", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _trees = MutableStateFlow<List<Tree>>(emptyList())
    val trees: StateFlow<List<Tree>> = _trees

    init {
        loadTrees()
    }

    // ---- SPECIES DATA (Native trees of Bengaluru) ----
    val speciesList: List<Species> = listOf(
        Species(
            id = "neem",
            nameEnglish = "Neem",
            nameKannada = "ಬೇವಿನ ಮರ",
            scientificName = "Azadirachta indica",
            description = "Neem is a fast-growing native tree known for its medicinal properties. It produces a large canopy providing excellent shade. A single mature neem tree can absorb up to 22 kg of CO₂ per year.",
            descriptionKannada = "ಬೇವು ಒಂದು ಔಷಧೀಯ ಗಿಡವಾಗಿದ್ದು, ಇದು ತ್ವರಿತವಾಗಿ ಬೆಳೆಯುತ್ತದೆ. ಇದು ವಾಯು ಶುದ್ಧೀಕರಣ ಮತ್ತು ನೆರಳು ನೀಡಲು ಹೆಸರುವಾಸಿ.",
            oxygenFactor = 1.8,
            isNative = true
        ),
        Species(
            id = "peepal",
            nameEnglish = "Peepal",
            nameKannada = "ಅರಳಿ ಮರ",
            scientificName = "Ficus religiosa",
            description = "The Peepal tree is sacred and one of the longest-living trees. It is unique as it releases oxygen even at night. It supports hundreds of bird and insect species.",
            descriptionKannada = "ಅರಳಿ ಮರ ಪವಿತ್ರ ಮರವಾಗಿದ್ದು ರಾತ್ರಿಯೂ ಆಮ್ಲಜನಕ ಬಿಡುಗಡೆ ಮಾಡುತ್ತದೆ. ಇದು ನೂರಾರು ಪಕ್ಷಿ ಮತ್ತು ಕೀಟ ಪ್ರಜಾತಿಗಳನ್ನು ಬೆಂಬಲಿಸುತ್ತದೆ.",
            oxygenFactor = 2.2,
            isNative = true
        ),
        Species(
            id = "honge",
            nameEnglish = "Honge",
            nameKannada = "ಹೊಂಗೆ ಮರ",
            scientificName = "Millettia pinnata",
            description = "Honge (Pongamia) is a native tree extensively used in urban forestry. Its seeds produce bio-diesel and it is highly drought-resistant. It provides dense shade and is a key species for restoring degraded land.",
            descriptionKannada = "ಹೊಂಗೆ ಮರ ನಗರ ವನ ಪ್ರದೇಶದಲ್ಲಿ ವ್ಯಾಪಕವಾಗಿ ಬಳಸಲಾಗುತ್ತದೆ. ಇದರ ಬೀಜಗಳಿಂದ ಜೈವಿಕ ಇಂಧನ ತಯಾರಿಸಲಾಗುತ್ತದೆ.",
            oxygenFactor = 1.6,
            isNative = true
        ),
        Species(
            id = "mango",
            nameEnglish = "Mango",
            nameKannada = "ಮಾವಿನ ಮರ",
            scientificName = "Mangifera indica",
            description = "Mango is the national tree of India. It provides thick shade, produces nutritious fruit and its leaves are used in festivals. Mature mango trees are important carbon sinks.",
            descriptionKannada = "ಮಾವಿನ ಮರ ಭಾರತದ ರಾಷ್ಟ್ರೀಯ ಮರ. ಇದು ಸಮೃದ್ಧ ಹಣ್ಣು ನೀಡುತ್ತದೆ ಮತ್ತು ದಟ್ಟ ನೆರಳು ಒದಗಿಸುತ್ತದೆ.",
            oxygenFactor = 1.5,
            isNative = true
        ),
        Species(
            id = "tamarind",
            nameEnglish = "Tamarind",
            nameKannada = "ಹುಣಸೆ ಮರ",
            scientificName = "Tamarindus indica",
            description = "Tamarind is a long-lived, slow-growing tree with a dense canopy. It is highly resistant to drought and supports many bird species. Its fruit is widely used in cooking.",
            descriptionKannada = "ಹುಣಸೆ ಮರ ದೀರ್ಘಕಾಲ ಬದುಕಬಲ್ಲ ಮರ. ಇದು ಬರ ನಿರೋಧಕ ಮತ್ತು ಅನೇಕ ಪಕ್ಷಿ ಪ್ರಜಾತಿಗಳಿಗೆ ಆಶ್ರಯ ನೀಡುತ್ತದೆ.",
            oxygenFactor = 1.7,
            isNative = true
        ),
        Species(
            id = "banyan",
            nameEnglish = "Banyan",
            nameKannada = "ಆಲದ ಮರ",
            scientificName = "Ficus benghalensis",
            description = "Banyan is India's national tree and one of the world's largest canopy trees. It provides habitat to a vast variety of species and its aerial roots are iconic. Mature trees contribute significantly to carbon sequestration.",
            descriptionKannada = "ಆಲದ ಮರ ಭಾರತದ ರಾಷ್ಟ್ರೀಯ ಮರ. ಇದು ಅನೇಕ ಪ್ರಾಣಿ ಮತ್ತು ಪಕ್ಷಿಗಳಿಗೆ ವಾಸಸ್ಥಳ ನೀಡುತ್ತದೆ.",
            oxygenFactor = 2.5,
            isNative = true
        )
    )

    // ---- TREE CRUD ----
    fun addTree(tree: Tree): Tree {
        val newTree = tree.copy(
            id = UUID.randomUUID().toString(),
            oxygenScore = calculateOxygenScore(tree.girth, tree.speciesName)
        )
        val current = _trees.value.toMutableList()
        current.add(newTree)
        _trees.value = current
        saveTrees(current)
        return newTree
    }

    fun deleteTree(treeId: String) {
        val current = _trees.value.filter { it.id != treeId }
        _trees.value = current
        saveTrees(current)
    }

    fun getTotalOxygenScore(): Double = _trees.value.sumOf { it.oxygenScore }

    fun getTreeCount(): Int = _trees.value.size

    fun getEmptyPitCount(): Int = _trees.value.count { it.isEmptyPit }

    // ---- OXYGEN SCORE FORMULA ----
    // Oxygen Score = Girth (cm) × Species Factor
    fun calculateOxygenScore(girth: Double, speciesName: String): Double {
        val species = speciesList.find {
            it.nameEnglish.equals(speciesName, ignoreCase = true) ||
                    it.nameKannada == speciesName
        }
        val factor = species?.oxygenFactor ?: 1.0
        return Math.round(girth * factor * 10.0) / 10.0
    }

    // ---- PERSISTENCE ----
    private fun saveTrees(trees: List<Tree>) {
        prefs.edit().putString("trees", gson.toJson(trees)).apply()
    }

    private fun loadTrees() {
        val json = prefs.getString("trees", null)
        if (json != null) {
            val type = object : TypeToken<List<Tree>>() {}.type
            _trees.value = gson.fromJson(json, type) ?: emptyList()
        }
    }
}