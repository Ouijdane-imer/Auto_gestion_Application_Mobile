import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.auto_gestion_v4.DataClass
import com.example.auto_gestion_v4.DetailledActivity
import com.example.auto_gestion_v4.R
import com.example.auto_gestion_v4.UpdateActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MyAdapter(private val context: Context, private var dataList: List<DataClass>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = dataList[position]
        Glide.with(context).load(data.dataImage)
            .into(holder.recImage)
        holder.recTitle.text = data.dataTitle
        holder.recCode.text = data.dataCode
        holder.quantite.text= data.quantity.toString()
        holder.prix.text= data.prix.toString()
        holder.recCard.setOnClickListener {
            val intent = Intent(context, DetailledActivity::class.java)
            intent.putExtra("Image", data.dataImage)
            intent.putExtra("Description", data.dataDesc)
            intent.putExtra("Nom", data.dataTitle)
            intent.putExtra("Code", data.dataCode)
            intent.putExtra("quantite",data.quantity)
            intent.putExtra("prix",data.prix)
            context.startActivity(intent)
        }
        holder.mMenus.setOnLongClickListener {
            holder.recCard.showContextMenu()
            true
        }

        holder.recCard.setOnCreateContextMenuListener { menu, v, menuInfo ->
            val popupMenu = PopupMenu(context, v)
            popupMenu.inflate(R.menu.show_menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.editText -> {
                        // Action à effectuer lorsque l'élément "Edit" est sélectionné

                        val selectedItem = dataList[holder.adapterPosition]
                        val intent = Intent(context, UpdateActivity::class.java)
                        intent.putExtra("ItemId", selectedItem.dataId)
                        context.startActivity(intent)
                        true
                    }

                    R.id.delete -> {
                        // Action à effectuer lorsque l'élément "Delete" est sélectionné
                        // Par exemple, vous pouvez afficher une boîte de dialogue de confirmation de suppression
                        val selectedItem = dataList[holder.adapterPosition]
                        val alertDialog = AlertDialog.Builder(context)
                            .setTitle("Supprimer")
                            .setMessage("Voulez-vous vraiment supprimer cet élément ?")
                            .setPositiveButton("Oui") { dialog, _ ->
                                // Implémentez ici la logique de suppression de l'élément
                                // Vous pouvez utiliser selectedItem.itemId pour identifier l'élément à supprimer
                                selectedItem.dataId?.let { deleteItem(it) }
                                Toast.makeText(context, "L'article est supprimé", Toast.LENGTH_SHORT).show() // Message affiché à l'utilisateur
                                dialog.dismiss()
                            }
                            .setNegativeButton("Non") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .create()
                        alertDialog.show()
                        true
                    }

                    else -> false
                }
            }

            popupMenu.show()
        }
    }


        override fun getItemCount(): Int {
        return dataList.size
    }
    private fun deleteItem(itemId: String) {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val itemsRef: DatabaseReference = database.getReference("Accessories")

        // Supprimer l'élément de la base de données
        itemsRef.child(itemId).removeValue()
    }

    fun searchDataList(searchList: List<DataClass>) {
        dataList = searchList
        notifyDataSetChanged()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var recImage: ImageView
        var recTitle: TextView
        var prix:TextView
        var recCode: TextView
        lateinit var quantite:TextView
        var recCard: CardView
        var mMenus:ImageView
        init {
            recImage = itemView.findViewById(R.id.recImage)
            recTitle = itemView.findViewById(R.id.recTitle)
            recCode = itemView.findViewById(R.id.recCode)
            quantite=itemView.findViewById(R.id.quantite)
            prix=itemView.findViewById(R.id.prix)
            recCard = itemView.findViewById(R.id.recCard)
            mMenus=itemView.findViewById(R.id.mMenus)
        }
    }
}
