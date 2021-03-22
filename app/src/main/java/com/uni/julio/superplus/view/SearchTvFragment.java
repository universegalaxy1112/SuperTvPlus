package com.uni.julio.superplus.view;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.leanback.app.BackgroundManager;
import androidx.leanback.app.SearchSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.OnItemViewSelectedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.leanback.widget.SpeechRecognitionCallback;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.uni.julio.superplus.LiveTvApplication;
import com.uni.julio.superplus.R;
import com.uni.julio.superplus.adapter.MoviesPresenter;
import com.uni.julio.superplus.listeners.DialogListener;
import com.uni.julio.superplus.listeners.StringRequestListener;
import com.uni.julio.superplus.model.MainCategory;
import com.uni.julio.superplus.model.ModelTypes;
import com.uni.julio.superplus.model.Movie;
import com.uni.julio.superplus.model.Serie;
import com.uni.julio.superplus.model.VideoStream;
import com.uni.julio.superplus.utils.DataManager;
import com.uni.julio.superplus.utils.Dialogs;
import com.uni.julio.superplus.utils.networing.LiveTVServicesManual;
import com.uni.julio.superplus.utils.networing.NetManager;
import com.uni.julio.superplus.utils.networing.WebConfig;
import com.uni.julio.superplus.view.exoplayer.VideoPlayFragment;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SearchTvFragment extends SearchSupportFragment implements SearchSupportFragment.SearchResultProvider {
    private static final int REQUEST_RECORD_AUDIO_STATE = 4;
    private static final int REQUEST_SPEECH = 16;
    private boolean denyAll = false;
    private BackgroundManager mBackgroundManager;
    private MainCategory mMainCategory;
    private ArrayObjectAdapter mRowsAdapter;
    protected int mainCategoryId;
    public List<VideoStream> movies;
    private Pattern pattern;
    protected ModelTypes.SelectedType selectedType;
    private DisplayMetrics mMetrics;
    private boolean isKeyboardShowing = false;

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        private ItemViewClickedListener() {
        }

        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof Serie) {
                Serie serie = (Serie) item;
                SearchTvFragment.this.addRecentSerie(serie);
                Bundle extras = new Bundle();
                extras.putSerializable("selectedType", ModelTypes.SelectedType.SERIES);
                extras.putInt("mainCategoryId", SearchTvFragment.this.mainCategoryId);
                extras.putString("serie", new Gson().toJson(serie));
                DataManager.getInstance().saveData("lastSerieSelected", new Gson().toJson(serie));
                launchActivity(LoadingActivity.class, extras);
            } else if (item instanceof Movie) {
                Movie movie = (Movie) item;
                Bundle extras2 = new Bundle();
                if (((Movie) item).getPosition() != -1) {
                    if (mainCategoryId == 4 || mainCategoryId == 7) {
                        onPlaySelectedDirect(movie, mainCategoryId);
                    } else {
                        extras2.putString("movie", new Gson().toJson(movie));
                        extras2.putInt("mainCategoryId", SearchTvFragment.this.mainCategoryId);
                        launchActivity(OneSeasonDetailActivity.class, extras2);
                        Objects.requireNonNull(requireActivity()).overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    }
                }
            }
        }
    }


    private void onPlaySelectedDirect(Movie movie, int mainCategoryId) {
        int movieId = movie.getContentId();
        String[] uris = {movie.getStreamUrl()};
        String[] extensions = {movie.getStreamUrl().substring(movie.getStreamUrl()./*replace(".mkv.mkv", ".mkv").replace(".mp4.mp4", ".mp4").*/lastIndexOf(".") + 1)};
        Intent launchIntent = new Intent(LiveTvApplication.appContext, VideoPlayActivity.class);
        launchIntent.putExtra(VideoPlayFragment.URI_LIST_EXTRA, uris)
                .putExtra(VideoPlayFragment.EXTENSION_LIST_EXTRA, extensions)
                .putExtra(VideoPlayFragment.MOVIE_ID_EXTRA, movieId)
                .putExtra(VideoPlayFragment.SECONDS_TO_START_EXTRA, 0L)
                .putExtra("mainCategoryId", mainCategoryId)
                .putExtra("type", 0)
                .putExtra("subsURL", movie.getSubtitleUrl())
                .putExtra("title", movie.getTitle())
                .setAction(VideoPlayFragment.ACTION_VIEW_LIST);
        ActivityCompat.startActivityForResult(Objects.requireNonNull(requireActivity()), launchIntent, 100, null);
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {
            try {
                if (item instanceof Movie && !(((Movie) item).getPosition() == -1)) {
                    SearchTvFragment.this.updateBackground(((Movie) item).getHDFondoUrl());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void addRecentSerie(Serie serie) {
        DataManager.getInstance().saveData("lastSerieSelected", new Gson().toJson(serie));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        container.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        Rect r = new Rect();
                        container.getWindowVisibleDisplayFrame(r);
                        int screenHeight = container.getRootView().getHeight();

                        // r.bottom is the position above soft keypad or device button.
                        // if keypad is shown, the r.bottom is smaller than that before.
                        int keypadHeight = screenHeight - r.bottom;
                        if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                            // keyboard is opened
                            if (!isKeyboardShowing) {
                                isKeyboardShowing = true;
                               // onKeyboardVisibilityChanged(true)
                            }
                        }
                        else {
                            // keyboard is closed
                            if (isKeyboardShowing) {
                                isKeyboardShowing = false;
                                //onKeyboardVisibilityChanged(false)
                            }
                        }
                    }
        });

        return super.onCreateView(inflater, container, savedInstanceState);

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = Objects.requireNonNull(requireActivity()).getIntent().getExtras();
        this.selectedType = (ModelTypes.SelectedType) Objects.requireNonNull(extras).get("selectedType");
        this.mainCategoryId = extras.getInt("mainCategoryId", 0);
        this.mMainCategory = new MainCategory();
        switch (this.mainCategoryId) {
            case 1:
                this.mMainCategory.setModelType(ModelTypes.SERIES_CATEGORIES);
                break;
            case 2:
                this.mMainCategory.setModelType(ModelTypes.SERIES_KIDS_CATEGORIES);
                break;
            case 3:
                this.mMainCategory.setModelType(ModelTypes.ENTERTAINMENT_CATEGORIES);
                break;
            case 4:
                this.mMainCategory.setModelType(ModelTypes.EVENTS_CATEGORIES);
                break;
            case 6:
                this.mMainCategory.setModelType(ModelTypes.KARAOKE_CATEGORIES);
                break;
            case 7:
                this.mMainCategory.setModelType(ModelTypes.ADULTS_CATEGORIES);
                break;
            default:
                this.mMainCategory.setModelType(ModelTypes.MOVIE_CATEGORIES);
                break;
        }
        this.movies = new ArrayList();
        this.pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        this.mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        prepareBackgroundManager();
        setSearchResultProvider(this);
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
        setOnItemViewClickedListener(new ItemViewClickedListener());
        if (getPermissionStatus() != 0) {
            setupAudioRecognition();
        } else {
            requestRecordAudioPermission();
        }
    }


    @Override
    public void startRecognition() {
        if(!SpeechRecognizer.isRecognitionAvailable(requireActivity())) return;
        try {
            if (getPermissionStatus() == 0) {
                super.startRecognition();
            } else {
                requestRecordAudioPermission();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void closeKeyboard() {
        Objects.requireNonNull(requireActivity()).getWindow().setSoftInputMode(2);
    }

    private void launchActivity(Class classToLaunch, Bundle extras) {
        Intent launchIntent = new Intent(requireActivity(), classToLaunch);
        launchIntent.putExtras(extras);
        startActivityForResult(launchIntent, 100);
        Objects.requireNonNull(requireActivity()).overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    @Override
    public ObjectAdapter getResultsAdapter() {
        return this.mRowsAdapter;
    }

    @Override
    public boolean onQueryTextChange(String newQuery) {
        return false;
    }


    private String removeSpecialChars(String s) {
        return this.pattern.matcher(Normalizer.normalize(s, Normalizer.Form.NFD)).replaceAll("");
    }

    @Override
    public boolean onQueryTextSubmit(final String query) {
        Objects.requireNonNull(requireActivity()).findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        LiveTVServicesManual.searchVideo(mMainCategory, removeSpecialChars(query), 45)
                .delay(2, TimeUnit.SECONDS, Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<VideoStream>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        try {
                            requireActivity();
                            Objects.requireNonNull(requireActivity()).findViewById(R.id.progressBar).setVisibility(View.GONE);
                            Log.d("error", "error");
                            Toast.makeText(requireActivity(), R.string.time_out, Toast.LENGTH_SHORT).show();
                            hideKeyboard();

                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }

                    }

                    @Override
                    public void onNext(List<VideoStream> videos) {
                        hideKeyboard();
                        requireActivity();
                        Objects.requireNonNull(requireActivity()).findViewById(R.id.progressBar).setVisibility(View.GONE);
                        movies = videos;
                        if (movies.size() < 1) {
                            Dialogs.showTwoButtonsDialog(requireActivity(), R.string.ok_dialog, R.string.cancel, R.string.title_order_message, new DialogListener() {

                                @Override
                                public void onAccept() {
                                    sendOrder(query);
                                }

                                @Override
                                public void onCancel() {

                                }

                                @Override
                                public void onDismiss() {

                                }
                            });
                            return;
                        }
                        SearchTvFragment.this.mRowsAdapter.clear();
                        SearchTvFragment.this.movies = videos;
                        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new MoviesPresenter(SearchTvFragment.this.requireActivity().getApplicationContext()));
                        listRowAdapter.addAll(0, SearchTvFragment.this.movies);
                        SearchTvFragment.this.mRowsAdapter.add(new ListRow(new HeaderItem("Resultados"), listRowAdapter));
                    }
                });
        return false;
    }

    private void hideKeyboard() {

        requireActivity();
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = requireActivity().getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(requireActivity());
        }
        if (imm != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    @Override
    public void onPause() {
        try {
            super.onPause();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendOrder(String query) {
        String reportUrl = WebConfig.orderUrl.replace("{USER}", LiveTvApplication.getUser() == null ? "" : LiveTvApplication.getUser().getName())
                .replace("{TIPO}", Integer.toString(mMainCategory.getId()))
                .replace("{TITLE}", query);
        NetManager.getInstance().makeStringRequest(reportUrl, new StringRequestListener() {
            @Override
            public void onCompleted(String response) {
                Toast.makeText(requireActivity(), "Thanks for requesting. We will add it as soon as possible!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError() {
                Toast.makeText(requireActivity(), "Failed to send request! Please check your network connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void requestRecordAudioPermission() {
        if (Build.VERSION.SDK_INT < 23 || getPermissionStatus() == 0) {
            return;
        }
        this.denyAll = false;
        int accept = R.string.accept;
        int message = R.string.permission_audio;
        if (getPermissionStatus() == 2) {
            this.denyAll = true;
            accept = R.string.config;
            message = R.string.permission_audio_config;
        }
        Dialogs.showTwoButtonsDialog(requireActivity(), accept, R.string.cancel, message, new DialogListener() {
            public void onAccept() {
                if (!SearchTvFragment.this.denyAll) {
                    DataManager.getInstance().saveData("audioPermissionRequested", Boolean.TRUE);
                    SearchTvFragment.this.requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, REQUEST_RECORD_AUDIO_STATE);
                    return;
                }
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", Objects.requireNonNull(SearchTvFragment.this.requireActivity()).getPackageName(), null));
                SearchTvFragment.this.startActivityForResult(intent, 4169);
            }

            public void onCancel() {
            }

            @Override
            public void onDismiss() {

            }
        });
    }

    private int getPermissionStatus() {
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(requireActivity()), "android.permission.RECORD_AUDIO") == 0) {
            return 0;
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), "android.permission.RECORD_AUDIO") || !DataManager.getInstance().getBoolean("audioPermissionRequested", false)) {
            return 1;
        }
        return 2;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SPEECH:
                if (resultCode == -1) {
                    setSearchQuery(data, true);
                    return;
                }
                return;
            case 4169:
                if(!SpeechRecognizer.isRecognitionAvailable(requireActivity())) return;
                if (getPermissionStatus() == 0) {
                    setupAudioRecognition();
                    return;
                }
                return;
            default:
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_RECORD_AUDIO_STATE && getPermissionStatus() == 0) {
            if(!SpeechRecognizer.isRecognitionAvailable(requireActivity())) return;
            setupAudioRecognition();
        }
    }

    private void setupAudioRecognition() {
        setSpeechRecognitionCallback(new SpeechRecognitionCallback() {
            public void recognizeSpeech() {
                try {
                    SearchTvFragment.this.startActivityForResult(SearchTvFragment.this.getRecognizerIntent(), REQUEST_SPEECH);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    Log.e("TAG", "Cannot find activity for speech recognizer", e);
                }
            }
        });
    }


    private boolean hasResults() {
        return this.mRowsAdapter.size() > 0;
    }

    private void updateBackground(final String uri) {
        try {
            requireActivity();
            Glide.with(this).load(uri).centerCrop().into(new SimpleTarget<Drawable>(this.mMetrics.widthPixels, this.mMetrics.heightPixels) {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    SearchTvFragment.this.mBackgroundManager.setDrawable(resource);

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void prepareBackgroundManager() {
        try {
            this.mBackgroundManager = BackgroundManager.getInstance(Objects.requireNonNull(requireActivity()));
            this.mBackgroundManager.attach(requireActivity().getWindow());
            this.mBackgroundManager.setColor(ContextCompat.getColor(requireActivity(), R.color.detail_background));
            mMetrics = new DisplayMetrics();
            requireActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != KeyEvent.KEYCODE_BACK || isKeyboardShowing) {
            return false;
        }
        requireActivity().finish();
        return true;
    }
}

