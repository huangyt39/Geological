package com.example.eaf.coresampleimgprocess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final String TAG = "MainFragment";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String GET_IMAGE_URL = "http://10.0.2.2:5000/getimage?imageindex=";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private List<SubImage> bitmapList = new ArrayList<>();
    private RecyclerView recyclerView = null;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
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
        // test recycle view
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_cancel);
//        bitmapList.add(new SubImage(bitmap));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) getView().findViewById(R.id.recycle_view_main_fragment);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        ImageAdapter imageAdapter = new ImageAdapter(bitmapList, (MainActivity)getActivity());
        recyclerView.setAdapter(imageAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
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


    class GetAllSplitImagesTask extends AsyncTask<String, Integer, String> {
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
            bitmapList.clear();
            int imageNumber = getImageNumber();
            for (int i=1; i<=imageNumber; i++) {
                Log.d(TAG, "doInBackground: start getting the " + String.valueOf(i) + "th image");
                Bitmap currentImage = getImage(String.valueOf(i));
                if (currentImage != null) {
                    bitmapList.add(new SubImage(currentImage));
                    Log.d(TAG, "doInBackground: add to bitmapList success");
                } else {
                    Log.d(TAG, "doInBackground: add to bitmapList error");
                }
            }
            Log.d(TAG, "doInBackground: the size of bitmapList finally " + String.valueOf(bitmapList.size()));
            return "success";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            recyclerView.getAdapter().notifyDataSetChanged();
            Log.d(TAG, "onPostExecute: final notify");
            Log.d(TAG, "onPostExecute: final notify the size of bitmapList " + String.valueOf(bitmapList.size()));
        }
    }


    class GetSplitImageTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
//            return null;
            Bitmap currentImage = getImage(strings[0]);
            if (currentImage != null) {
                bitmapList.add(new SubImage(currentImage));
                Log.d(TAG, "doInBackground: add to bitmapList success");
                return "success";
            } else {
                return "error";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute: notify change");
            Log.d(TAG, "onPostExecute: ");
            Log.d(TAG, "onPostExecute: the size of bitmap " + String.valueOf(bitmapList.size()));
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    private Bitmap getImage(String imageIndex) {
        Log.d(TAG, "getImage: getting " + imageIndex);
        String imageUrl = GET_IMAGE_URL + imageIndex;
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

    private int getImageNumber() {
        Log.d(TAG, "getImageNumber: start");
        String imageNumberUrl = "http://10.0.2.2:5000/getimagesnumber";
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(imageNumberUrl).get().build();
        try {
            Response response = MainActivity.okHttpClientWithCookie.newCall(request).execute();
            String s = response.body().string();
            Log.d(TAG, "getImageNumber: " + s);
            return Integer.parseInt(s);
        } catch (IOException e) {
            Log.d(TAG, "getImageNumber: error in getimage number");
            e.printStackTrace();
            Log.d(TAG, "getImageNumber: error end");
            return 0;
        }
    }

    public void loadPictureFromServer() {
        new GetAllSplitImagesTask().execute();
    }

    public void testFunction() {
//        Toast.makeText(getActivity(), "on upload photTo result", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "testFunction: gggggggggggggggggggggggggggggggggggggggggggg");
    }
}
