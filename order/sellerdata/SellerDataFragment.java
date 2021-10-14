package ru.kazachkov.florist.order.sellerdata;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import ru.kazachkov.florist.NewOrderActivity;
import ru.kazachkov.florist.R;
import ru.kazachkov.florist.adapters.FloristSpinnerAdapter;
import ru.kazachkov.florist.api.model.Category;
import ru.kazachkov.florist.api.model.CompanyAtGlanceDT;
import ru.kazachkov.florist.api.model.User1CIdDT;
import ru.kazachkov.florist.databinding.FragmentSellerDataBinding;
import ru.kazachkov.florist.databinding.ListItemUserSearchBinding;
import ru.kazachkov.florist.tools.rx.SearchViewQueryTextChangesOnSubscribe;
import rx.Observable;

import static ru.kazachkov.florist.tools.Preconditions.checkNotNull;


public class SellerDataFragment extends Fragment implements SellerDataContract.View {


    private SellerDataContract.Presenter presenter;
    private SellerDataViewModel viewModel;
    private FloristSpinnerAdapter<User1CIdDT> authorsAdapter;
    private FloristSpinnerAdapter<User1CIdDT> responsibleAdapter;
    private ClientsAdapter adapter;
    private SearchView searchView;
    private RecyclerView clientsRecyclerView;
    private FragmentSellerDataBinding sellerDataBinding;
    private FloristSpinnerAdapter<Category> categoryFloristSpinnerAdapter;

    public SellerDataFragment() {
    }

    public static SellerDataFragment newInstance() {

        Bundle args = new Bundle();

        SellerDataFragment fragment = new SellerDataFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        sellerDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_seller_data, container, false);

        sellerDataBinding.setViewModel(viewModel);
        sellerDataBinding.setActionHandler(presenter);
        Spinner spinner = sellerDataBinding.author;

        authorsAdapter = new FloristSpinnerAdapter<>(presenter.getAuthors());
        spinner.setAdapter(authorsAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                presenter.setSelectedAuthor(authorsAdapter.getItem(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Spinner responsibleSpinner = sellerDataBinding.responsible;

        responsibleAdapter = new FloristSpinnerAdapter<>(presenter.getAuthors());
        responsibleSpinner.setAdapter(responsibleAdapter);

        responsibleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                presenter.setSelectedAuthor(responsibleAdapter.getItem(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        searchView = sellerDataBinding.searchView;

        adapter = new ClientsAdapter(new ArrayList<>(0), presenter);
        clientsRecyclerView = sellerDataBinding.clientsRecyclerView;
        clientsRecyclerView.setAdapter(adapter);

        searchView.setOnCloseListener(() -> {
            this.closeSearch();
            return false;
        });

        categoryFloristSpinnerAdapter = new FloristSpinnerAdapter<>(new ArrayList<Category>(0));
        sellerDataBinding.categoriesSpinner.setAdapter(categoryFloristSpinnerAdapter);
        sellerDataBinding.categoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                presenter.setCategory(categoryFloristSpinnerAdapter.getItem(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sellerDataBinding.textBayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.refreshSubscriptions(true);
            }
        });
        return sellerDataBinding.getRoot();
    }

    @Override
    public void hideSearchPanel() {
        sellerDataBinding.clientsFrameLayout.setVisibility(View.GONE);
        viewModel.setShowInfoPanel(true);
    }

    @Override
    public void showSearchPanel() {
        sellerDataBinding.clientsFrameLayout.setVisibility(View.VISIBLE);
        viewModel.setShowInfoPanel(false);
    }

    @Override
    public void showSearchViewWithText(CompanyAtGlanceDT currentClient) {
        sellerDataBinding.clientTableRow.setVisibility(View.GONE);
        sellerDataBinding.searchTableRow.setVisibility(View.VISIBLE);

        String str = "";
        if(currentClient != null)
            str = currentClient.getCompanyName();

        searchView.setQuery(str, false);
        searchView.setIconified(false);

        EditText edit = (EditText)searchView.findViewById(R.id.search_src_text);
        edit.selectAll();

        searchView.requestFocus();

        showSearchPanel();
    }

    @Override
    public void showClientInfo(CompanyAtGlanceDT client) {
        closeSearch();
        sellerDataBinding.clientTextView.setText(client.getCompanyName());
        sellerDataBinding.clientContactTextView.setText(client.getTelNumber());
    }

    private void closeSearch() {
        searchView.clearFocus();
        sellerDataBinding.clientTableRow.setVisibility(View.VISIBLE);
        sellerDataBinding.searchTableRow.setVisibility(View.GONE);

        hideSearchPanel();
    }


    @Override
    public void showCategories(List<Category> categories) {
        categoryFloristSpinnerAdapter.replaceData(categories);
    }

    @Override
    public void setPresenter(@NonNull SellerDataContract.Presenter presenter) {
        this.presenter = checkNotNull(presenter);
    }


    @Override
    public void onResume() {
        super.onResume();
        presenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.unsubscribe();
    }


    public void setViewModel(SellerDataViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void showAddClientDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        AddClientDialog addClientDialog = new AddClientDialog();
        addClientDialog.show(fm, "fragment_add_client");
        addClientDialog.setOnResultListener(presenter);
    }

    @Override
    public void showFirstCompanyInSellerData(CompanyAtGlanceDT company) {
        viewModel.setClientName(company.getCompanyName());
        viewModel.setClientPhoneNumber(company.getTelNumber());
    }

    @Override
    public void showErrorLoadingCompanies() {

    }


    @Override
    public void showAuthors(List<User1CIdDT> user1CIdDTs, int currentAuthorPosition) {
        authorsAdapter.replaceData(user1CIdDTs);
        responsibleAdapter.replaceData(user1CIdDTs);
    }

    @Override
    public void showError(Throwable throwable) {

    }

    @Override
    public void showClients(List<CompanyAtGlanceDT> companyAtGlanceDTs) {
        adapter.setClients(companyAtGlanceDTs);
    }

    @Override
    public Observable<CharSequence> getSearchObs() {
        return Observable.create(new SearchViewQueryTextChangesOnSubscribe(searchView));
    }


    private static class ClientsAdapter extends RecyclerView.Adapter<ClientsAdapter.ClientViewHolder> {

        List<CompanyAtGlanceDT> clients;
        SellerDataContract.Presenter presenter;


        ClientsAdapter(List<CompanyAtGlanceDT> clients, SellerDataContract.Presenter presenter) {
            setClients(clients);
            this.presenter = presenter;
        }

        private void setClients(List<CompanyAtGlanceDT> clients) {
            this.clients = clients;
            notifyDataSetChanged();
        }

        @Override
        public ClientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new ClientViewHolder(inflater.inflate(R.layout.list_item_user_search, parent, false));
        }

        @Override
        public void onBindViewHolder(ClientViewHolder holder, int position) {
            holder.binding.setClient(clients.get(position));
            holder.binding.setPresenter(presenter);
            holder.binding.executePendingBindings();
        }

        @Override
        public int getItemCount() {
            return clients != null ? clients.size() : 0;
        }

        class ClientViewHolder extends RecyclerView.ViewHolder {
            ListItemUserSearchBinding binding;

            ClientViewHolder(View itemView) {
                super(itemView);
                this.binding = DataBindingUtil.bind(itemView);
            }
        }
    }

}
