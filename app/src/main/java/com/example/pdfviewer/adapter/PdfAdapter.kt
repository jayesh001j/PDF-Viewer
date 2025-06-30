package com.example.pdfviewer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pdfviewer.R
import com.example.pdfviewer.model.PdfModel

class PdfAdapter(
    private val onItemClick: (PdfModel) -> Unit,
    private val onFavoriteClick: (PdfModel) -> Unit
) : RecyclerView.Adapter<PdfAdapter.PdfViewHolder>() {

    private val pdfList = mutableListOf<PdfModel>()

    inner class PdfViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.pdfName)
        val favIcon: ImageView = itemView.findViewById(R.id.favIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pdf_tile, parent, false)
        return PdfViewHolder(view)
    }

    override fun onBindViewHolder(holder: PdfViewHolder, position: Int) {
        val pdf = pdfList[position]
        holder.name.text = pdf.name
        holder.favIcon.setImageResource(
            if (pdf.isFavorite) R.drawable.ic_like else R.drawable.ic_unlike
        )

        holder.itemView.setOnClickListener { onItemClick(pdf) }
        holder.favIcon.setOnClickListener {
            onFavoriteClick(pdf)
            notifyItemChanged(position)
        }
    }

    override fun getItemCount() = pdfList.size

    fun updateList(newList: List<PdfModel>) {
        pdfList.clear()
        pdfList.addAll(newList)
        notifyDataSetChanged()
    }
}
