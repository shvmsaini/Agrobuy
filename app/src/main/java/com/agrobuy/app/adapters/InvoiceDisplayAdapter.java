package com.agrobuy.app.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agrobuy.app.object.Invoice;
import com.agrobuy.app.R;

import java.util.List;

public class InvoiceDisplayAdapter extends RecyclerView.Adapter<InvoiceDisplayAdapter.ItemViewHolder> {
    private static final String LOG_TAG = InvoiceDisplayAdapter.class.getName();
    public List<Invoice> invoiceList;
    public Context mContext;

    public InvoiceDisplayAdapter(android.content.Context context, List<Invoice> invoiceList) {
        this.invoiceList = invoiceList;
        this.mContext = context;
    }
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.invoice_item,parent,false);
        ItemViewHolder viewHolder = new ItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Log.d(LOG_TAG,"bindViewHolder");
        Invoice invoice = invoiceList.get(position);
        if(invoice.getInvoiceURL()!=null){
            holder.invoiceNumber.setText(invoice.getInvoiceNumber());
            holder.documentLink.setText(Html.fromHtml(invoice.getInvoiceURL()));
            Linkify.addLinks(holder.documentLink,Linkify.ALL);
            holder.documentLink.setOnLongClickListener(v -> {
                ClipboardManager cm = (ClipboardManager)mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newHtmlText(holder.documentLink.getText().toString(),
                        holder.documentLink.getText().toString(),holder.documentLink.getText().toString());
                cm.setPrimaryClip(clip);
                Toast.makeText(mContext, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                return true;
            });
            //hiding all other views
            holder.customerNameP.setVisibility(View.GONE);
            holder.invoiceAmountP.setVisibility(View.GONE);
            holder.invoiceDueDateP.setVisibility(View.GONE);
            holder.deliveryModeP.setVisibility(View.GONE);
            holder.deliveryDestinationP.setVisibility(View.GONE);
            holder.paymentTermsP.setVisibility(View.GONE);
        }
        else{
            holder.invoiceNumber.setText(invoice.getInvoiceNumber());
            holder.customerName.setText(invoice.getCustomerName());
            holder.invoiceAmount.setText(invoice.getInvoiceAmount());
            holder.invoiceDueDate.setText(invoice.getInvoiceDueDate());
            holder.deliveryMode.setText(invoice.getDeliveryMode());
            holder.deliveryDestination.setText(invoice.getDeliveryDestination());
            holder.paymentTerms.setText(invoice.getPaymentTerms());
            // hiding all other views
            holder.documentLinkP.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return invoiceList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView invoiceNumber, customerName, invoiceAmount, invoiceDueDate, paymentTerms,
                deliveryMode,deliveryDestination,documentLink;
        View customerNameP, invoiceAmountP, invoiceDueDateP, paymentTermsP,
                deliveryModeP,deliveryDestinationP,documentLinkP;
        public ItemViewHolder(View itemView) {
            super(itemView);
            invoiceNumber = itemView.findViewById(R.id.invoice_number);
            customerName = itemView.findViewById(R.id.customer_name);
            invoiceAmount = itemView.findViewById(R.id.invoice_amount);
            invoiceDueDate = itemView.findViewById(R.id.invoice_due_date);
            paymentTerms = itemView.findViewById(R.id.terms);
            deliveryMode = itemView.findViewById(R.id.delivery_mode);
            deliveryDestination = itemView.findViewById(R.id.delivery_destination);
            documentLink = itemView.findViewById(R.id.document_link);
            // parents
            customerNameP = itemView.findViewById(R.id.invoice_rl2);
            invoiceAmountP = itemView.findViewById(R.id.invoice_rl3);
            invoiceDueDateP = itemView.findViewById(R.id.invoice_rl4);
            paymentTermsP = itemView.findViewById(R.id.invoice_rl5);
            deliveryModeP = itemView.findViewById(R.id.invoice_rl6);
            deliveryDestinationP = itemView.findViewById(R.id.invoice_rl7);
            documentLinkP = itemView.findViewById(R.id.invoice_rl8);
        }

    }
}
