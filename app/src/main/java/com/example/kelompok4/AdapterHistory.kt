package com.example.kelompok4

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import org.json.JSONObject

class AdapterHistory (val context: Context, val userListt: ArrayList<DataHistory>):
    RecyclerView.Adapter<AdapterHistory.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.list_history, parent, false)

        return MyViewHolder(itemView)


    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem=userListt[position]
        val backgroundColor = if (currentItem.tipe == "pemasukan") {
            Color.parseColor("#D0FFD7")
        } else {
            Color.parseColor("#FEFFED")
        }
        holder.textharga.text=currentItem.harga.toString()
        holder.textketerangan.text=currentItem.keterangan
        holder.texttanggal.text=currentItem.tanggal
        holder.texttipe.text=currentItem.tipe
        holder.cardHistory.setCardBackgroundColor(backgroundColor)
        val id = currentItem.id
        holder.btnDelete.setOnClickListener {

            val alertDialog = AlertDialog.Builder(context)
            alertDialog.setTitle("Konfirmasi")
            alertDialog.setMessage("Apakah Anda yakin ingin menghapus item ini?")

            alertDialog.setPositiveButton("Ya") { _, _ ->
                deleteTabungan(id)
                userListt.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, userListt.size)
            }

            alertDialog.setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = alertDialog.create()
            dialog.show()
        }
        holder.btnEdit.setOnClickListener {

            val intent = Intent(context, UpdateActivity::class.java)
            intent.putExtra("idHistory", id.toString())
            context.startActivity(intent)
            Log.d("ini dari adapter", id.toString())
        }


    }
    fun deleteTabungan(id: Int) {
        val url = urlAPI.endpoint.url
        AndroidNetworking.delete("$url/api/tabungan/delete/$id")
            .addHeaders("Content-Type", "application/json")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    Toast.makeText(context,"Berhasil Menghapus",Toast.LENGTH_SHORT).show()

                }

                override fun onError(anError: ANError) {
                    Toast.makeText(context,"Gagal Menghapus ${anError.errorCode}",Toast.LENGTH_SHORT).show()

                }
            })
    }

    override fun getItemCount(): Int {
        return userListt.size
    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val cardHistory : CardView=itemView.findViewById(R.id.cardHistory)
        val textharga : TextView =itemView.findViewById(R.id.history_harga)
        val textketerangan : TextView = itemView.findViewById(R.id.history_keterangan)
        val texttanggal : TextView =itemView.findViewById(R.id.history_tanggal)
        val texttipe: TextView =itemView.findViewById(R.id.history_tipe)
        val btnDelete : LinearLayout=itemView.findViewById(R.id.btnDelete)
        val btnEdit :LinearLayout=itemView.findViewById(R.id.btnEdit)


    }
}