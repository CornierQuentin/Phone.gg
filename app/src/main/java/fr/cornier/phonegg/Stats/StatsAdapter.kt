package fr.cornier.phonegg.Stats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.cornier.phonegg.R

class StatsAdapter: RecyclerView.Adapter<StatsAdapter.StatsViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StatsAdapter.StatsViewHolder {
        val rootView =
            LayoutInflater.from(parent.context).inflate(R.layout.cell_stats, parent, false)

        return StatsViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: StatsAdapter.StatsViewHolder, position: Int) {
        holder.fillCell(position)
    }

    override fun getItemCount(): Int {
        return 4
    }

    inner class StatsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun fillCell(position: Int) {

        }
    }
}