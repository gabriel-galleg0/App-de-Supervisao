package Model;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.appjava.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;

    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_contact, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.textViewName.setText(user.getNome());
        holder.textViewPhoneNumber.setText(user.getphoneNumber());
        holder.botaoWhats.setOnClickListener(v ->{
            String phoneNumber = user.getphoneNumber();
            abrirWhatsApp(v.getContext(), phoneNumber);
        });
    }

    private void abrirWhatsApp(Context context, String phoneNumber) {
        String url = "https://api.whatsapp.com/send?phone=" + phoneNumber;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textViewPhoneNumber;
        Button botaoWhats;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            botaoWhats = itemView.findViewById(R.id.btnWhats);
            textViewName = itemView.findViewById(R.id.nomeVendedorCt);
            textViewPhoneNumber = itemView.findViewById(R.id.telefoneVendedorCt);
        }
    }
}
