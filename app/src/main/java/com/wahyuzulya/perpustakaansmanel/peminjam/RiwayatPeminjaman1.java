package com.wahyuzulya.perpustakaansmanel.peminjam;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wahyuzulya.perpustakaansmanel.CustomVolleyRequestQueue;
import com.wahyuzulya.perpustakaansmanel.DbContract;
import com.wahyuzulya.perpustakaansmanel.MainActivity;
import com.wahyuzulya.perpustakaansmanel.R;
import com.wahyuzulya.perpustakaansmanel.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RiwayatPeminjaman1 extends AppCompatActivity {
    Toolbar toolbar;
    ArrayList<RiwayatObjek1> list1;
    ListView listView;
    ImageButton ibtn_logout;
    Button btn_pinjamBuku;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_peminjaman1);
        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.tl_peminjam);
        listView = findViewById(R.id.lv_buku);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        final SessionManager sessionManager = new SessionManager(getApplicationContext());

        ibtn_logout = (ImageButton) findViewById(R.id.ibtn_logout);
        ibtn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Keluar");
                builder.setMessage("Kamu yakin ingin keluar?");
                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sessionManager.setLogin(false);
                        sessionManager.setUsername("");
                        startActivity(new Intent(RiwayatPeminjaman1.this, MainActivity.class));
                        finish();
                    }
                });
                builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Toast.makeText(getApplicationContext(),sessionManager.getId(),Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        btn_pinjamBuku = (Button) findViewById(R.id.btn_pinjamBuku);
        btn_pinjamBuku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RiwayatPeminjaman1.this, PeminjamPinjam.class));
                finish();
            }
        });
        tampilData(sessionManager.getId(),sessionManager.getStatus());
    }



    void tampilData(final String idpeminjam, final String statuspem){
        list1 = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.SERVER_COLLECTDATA_RIWAYAT_PEMINJAMAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i=0; i<jsonArray.length();i++){
                                JSONObject getData=jsonArray.getJSONObject(i);
                                String nomorBuku = getData.getString("nomorbuku");
                                String judul = getData.getString("judul");
                                String tanggalPinjam = getData.getString("tanggalpinjam");
                                String tanggalKembali = getData.getString("tanggalkembali");
                                String denda = getData.getString("denda");
                                String img = "https://wahyuzly.000webhostapp.com/page/buku/"+judul+".jpg";
                                String statusBuku= getData.getString("status");

                                //Toast.makeText(getApplicationContext(),kategori+" "+kelas+" "+jumlah,Toast.LENGTH_LONG).show();
                                list1.add(new RiwayatObjek1(nomorBuku,judul,tanggalPinjam,tanggalKembali,denda,img,statusBuku));
                                //Toast.makeText(getApplicationContext(),list.toString(),Toast.LENGTH_LONG).show();
                            }
                            AdapterRiwayat1 adapterRiwayat1 = new AdapterRiwayat1(RiwayatPeminjaman1.this,list1);
                            listView.setAdapter(adapterRiwayat1);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("status", statuspem);
                params.put("id_peminjam",idpeminjam);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}

class AdapterRiwayat1 extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    ArrayList<RiwayatObjek1> model;
    public AdapterRiwayat1(Context context, ArrayList<RiwayatObjek1>model){
        inflater=LayoutInflater.from(context);
        this.context=context;
        this.model = model;

    }

    @Override
    public int getCount() {
        return model.size();
    }

    @Override
    public Object getItem(int position) {
        return model.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    TextView nomorBuku,judulBuku,tglPinjam,tglKembali,denda,keterangan;
    RelativeLayout ll_riwayat;
    NetworkImageView gambar;
    ImageLoader imageLoader;
    Button btnSelesai;

    @SuppressLint("Range")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.riwayat_peminjaman,parent,false);
        nomorBuku=view.findViewById(R.id.tv_noBuku);
        judulBuku=view.findViewById(R.id.tv_judulBuku);
        tglPinjam=view.findViewById(R.id.tv_tgl_pinjam);
        tglKembali=view.findViewById(R.id.tv_tgl_kembali);
        denda=view.findViewById(R.id.tv_denda);
        gambar=view.findViewById(R.id.niv_gambar);
        btnSelesai = view.findViewById(R.id.btn_selesai);
        keterangan = view.findViewById(R.id.tv_keterangan);
        ll_riwayat = view.findViewById(R.id.ll_riwayat);

        nomorBuku.setText(model.get(position).getNomorBuku());
        judulBuku.setText(model.get(position).getJudul());
        tglPinjam.setText("Tanggal Pinjam\t:"+model.get(position).getTanggalPinjam());
        tglKembali.setText("Tanggal Kembali\t: "+model.get(position).getTanggalKembali());
        denda.setText("Denda\t: "+model.get(position).getDenda());
        String url = model.get(position).getImg();
        imageLoader = CustomVolleyRequestQueue.getInstance(context.getApplicationContext()).getImageLoader();
        imageLoader.get(url,ImageLoader.getImageListener(gambar,R.mipmap.ic_launcher,R.drawable.ic_launcher_background));
        gambar.setImageUrl(url,imageLoader);
        /*if (Integer.parseInt(model.get(position).getJumlah())==0){
            btnPinjam.setText("Habis");
            btnPinjam.setEnabled(false);
        }*/
        String statusBuku = model.get(position).getStatusBuku();
        if (statusBuku.equals("v")){
            btnSelesai.setVisibility(View.GONE);
            keterangan.setVisibility(View.VISIBLE);
            keterangan.setText("[TRANSAKSI TELAH SELESAI]");
            ll_riwayat.setBackgroundColor(Color.rgb(191, 191, 191));
        } else {
            if (statusBuku.equals("e")){
                keterangan.setVisibility(View.VISIBLE);
                keterangan.setText("[MENUNGGU KONFIRMASI STAF]\nNote: Silahkan temui staf dan kembalikan buku.");
                btnSelesai.setText("BATAL");
                ll_riwayat.setBackgroundColor(Color.rgb(240, 240, 240));
                btnSelesai.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (btnSelesai.isEnabled()) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                            builder.setTitle("Pengembalian");
                            builder.setMessage("Kamu yakin ingin membatalkan pengembalian buku ini?");
                            builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();

                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    }
                });
            } else if (statusBuku.equals("s")){
                btnSelesai.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (btnSelesai.isEnabled()) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                            builder.setTitle("Pengembalian");
                            builder.setMessage("Kamu yakin ingin melakukan pengembalian buku ini?");
                            builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();

                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    }
                });
            }

        }

        return view;
    }

}