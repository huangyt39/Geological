package com.example.eaf.coresampleimgprocess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TPFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TPFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TPFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String GET_TPResult_URL = "http://47.107.126.23:5000/getpredictresult?predictresultindex=";
    private static final String TAG = "TPFragment";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageView tpResult1;
    private ImageView tpResult2;
    private Bitmap bitmap1;
    private Bitmap bitmap2;
    private TextView hint;
    private OnFragmentInteractionListener mListener;

    public TPFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TPFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TPFragment newInstance(String param1, String param2) {
        TPFragment fragment = new TPFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tpResult1=getView().findViewById(R.id.tpResult1);
        tpResult2=getView().findViewById(R.id.tpResult2);
        hint=getView().findViewById(R.id.hint);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tp, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }



    class GetTPResultTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
//            Log.d(TAG, "doInBackground: get all image begin image number : " + strings[0]);
//            bitmapList.clear();
//            int imageNumber = getImageNumber();
//            for (int i=1; i<=imageNumber; i++) {
//                Log.d(TAG, "doInBackground: start getting the " + String.valueOf(i) + " th image");
//                new GetSplitImageTask().execute(String.valueOf(i));
//            }
//            return "success";
            Log.d(TAG, "doInBackground: get all image start");
            Log.d(TAG, "doInBackground: start getting the " + String.valueOf(0) + "th image");
            bitmap1 = getTPResult(String.valueOf(0));
            bitmap2=getTPResult(String.valueOf(1));
            return "success";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            tpResult1.setImageBitmap(bitmap1);
            tpResult2.setImageBitmap(bitmap2);
            hint.setText("Terrain prediction result:");
            Log.d(TAG, "onPostExecute: final notify");
        }
    }

    private Bitmap getTPResult(String imageIndex) {
        Log.d(TAG, "getImage: getting " + imageIndex);
        String imageUrl = GET_TPResult_URL + imageIndex;
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(imageUrl).get().build();
        try {
            Response response = MainActivity.okHttpClientWithCookie.newCall(request).execute();
            InputStream inputStream = response.body().byteStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            Log.d(TAG, "getImage: getting " + imageIndex + " done");
            return bitmap;
        } catch (IOException e) {
            Log.d(TAG, "getImage: error in getImage");
            e.printStackTrace();
            Log.d(TAG, "getImage: error end");
            return null;
        }

    }

    public void loadTPResultFromServer(){
        new TPFragment.GetTPResultTask().execute();
        hint.setText("Loading result...");
    }


}
