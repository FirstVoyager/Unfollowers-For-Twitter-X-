package limitless.android.unfollowtita.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import limitless.android.unfollowtita.Other.Listener;
import limitless.android.unfollowtita.Model.Account;
import limitless.android.unfollowtita.Other.Utils;
import limitless.android.unfollowtita.R;
import limitless.android.unfollowtita.databinding.ItemAccountBinding;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {

    private Context context;
    private List<Account> accounts;
    private Listener<Account> accountListener;

    public AccountAdapter(@NonNull Context context, @Nullable List<Account> accounts, @Nullable Listener<Account> accountListener) {
        this.context = context;
        this.accounts = accounts;
        this.accountListener = accountListener;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AccountViewHolder(LayoutInflater.from(context).inflate(R.layout.item_account, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        holder.bindView(accounts.get(position));
    }

    @Override
    public int getItemCount() {
        try {
            return accounts.size();
        }catch (Exception e){
            Utils.error(e);
            return 0;
        }
    }

    class AccountViewHolder extends RecyclerView.ViewHolder {
        private ItemAccountBinding binding;
        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemAccountBinding.bind(itemView);
            binding.getRoot().setOnClickListener(v -> {
                if (accountListener != null)
                    accountListener.data(accounts.get(getAdapterPosition()));
            });
        }

        void bindView(Account a){
            Utils.loadPhoto(context, a.profileUrl, binding.imageViewAvatar, true);
            binding.textViewName.setText(a.name);
            binding.textViewScreen.setText("@");
            binding.textViewScreen.append(a.screenName);
            if (a.isMain)
                binding.viewMain.setVisibility(View.VISIBLE);
            else
                binding.viewMain.setVisibility(View.INVISIBLE);
        }

    }

}
