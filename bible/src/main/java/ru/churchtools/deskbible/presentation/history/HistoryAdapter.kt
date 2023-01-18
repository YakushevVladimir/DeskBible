package ru.churchtools.deskbible.presentation.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.BibleQuote.R
import com.BibleQuote.entity.ItemList

class HistoryAdapter(
    private val items: List<ItemList>,
    private val clickListener: (ItemList) -> Unit,
    ) : RecyclerView.Adapter<HistoryAdapter.HistoryHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_item, parent, false)
        return HistoryHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryHolder, position: Int) {
        holder.link.text = items[position][ItemList.Name]
        holder.link.setOnClickListener {
            clickListener(items[position])
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class HistoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val link: TextView = itemView.findViewById(R.id.place_scripture)
    }
}

