package com.hasiruusiru.app.ui.speciesguide
import com.hasiruusiru.app.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hasiruusiru.app.data.model.Species

class SpeciesAdapter(
    private val speciesList: List<Species>,
    private var isKannada: Boolean
) : RecyclerView.Adapter<SpeciesAdapter.SpeciesViewHolder>() {

    inner class SpeciesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_species_name)
        val tvScientific: TextView = itemView.findViewById(R.id.tv_scientific_name)
        val tvDescription: TextView = itemView.findViewById(R.id.tv_description)
        val tvOxygenFactor: TextView = itemView.findViewById(R.id.tv_oxygen_factor)
        val tvNative: TextView = itemView.findViewById(R.id.tv_native)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpeciesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_species, parent, false)
        return SpeciesViewHolder(view)
    }

    override fun onBindViewHolder(holder: SpeciesViewHolder, position: Int) {
        val species = speciesList[position]
        holder.tvName.text = if (isKannada) species.nameKannada else species.nameEnglish
        holder.tvScientific.text = species.scientificName
        holder.tvDescription.text = if (isKannada) species.descriptionKannada else species.description
        holder.tvOxygenFactor.text = "O₂ Factor: ${species.oxygenFactor}"
        holder.tvNative.text = if (species.isNative) "🌿 Native Species" else "🌍 Non-Native"
    }

    override fun getItemCount() = speciesList.size

    fun toggleLanguage(kannada: Boolean) {
        isKannada = kannada
        notifyDataSetChanged()
    }
}
