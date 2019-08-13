package agency.digitera.android.promdate.ui.dress.adapter

import agency.digitera.android.promdate.R
import agency.digitera.android.promdate.data.models.Dress
import agency.digitera.android.promdate.util.LoadUrl
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_dress.view.*

//TODO: Convert this to an infinitely-scrolling list, like singles and couples
class DressAdapter(
    private val dresses: ArrayList<Dress>,
    private val clickListener: (Dress) -> Unit
) : RecyclerView.Adapter<DressAdapter.ViewHolder>() {

    //sets content of view
    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(dress: Dress, clickListener: (Dress) -> Unit) {
            with(itemView) {
                name_text.text = dress.name
                model_text.text = context.getString(R.string.model_number, dress.modelNumber)
                LoadUrl.loadDressPicture(context, dress_image, dress.imgUrl)
                setOnClickListener { clickListener(dress) }
            }
        }
    }

    //create new view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dress, parent, false) as View

        return ViewHolder(itemView)
    }

    //replace contents of view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dresses[position], clickListener)
    }

    //return size of dataset
    override fun getItemCount() = dresses.size
}