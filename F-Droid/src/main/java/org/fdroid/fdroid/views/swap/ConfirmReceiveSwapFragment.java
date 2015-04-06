package org.fdroid.fdroid.views.swap;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.fdroid.fdroid.ProgressListener;
import org.fdroid.fdroid.R;
import org.fdroid.fdroid.UpdateService;
import org.fdroid.fdroid.data.NewRepoConfig;
import org.fdroid.fdroid.data.Repo;
import org.fdroid.fdroid.data.RepoProvider;

public class ConfirmReceiveSwapFragment extends Fragment implements ProgressListener {

    private static final String TAG = "fdroid.ConfirmReceiveSwapFragment";

    private NewRepoConfig newRepoConfig;

    @Nullable
    private Repo repo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.swap_confirm_receive, container, false);

        view.findViewById(R.id.no_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        view.findViewById(R.id.yes_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });

        return view;
    }

    private void finish() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    public void onResume() {
        super.onResume();
        newRepoConfig = new NewRepoConfig(getActivity(), getActivity().getIntent());
        if (newRepoConfig.isValidRepo()) {
            ((TextView) getView().findViewById(R.id.text_description)).setText(
                getString(R.string.swap_confirm_connect, newRepoConfig.getHost())
            );
        } else {
            // TODO: Show error message on screen (not in popup).
        }
    }

    private void confirm() {
        this.repo = ensureRepoExists();
        UpdateService.updateRepoNow(this.repo.address, getActivity()).setListener(this);
    }

    @NonNull
    private Repo ensureRepoExists() {
        // TODO: newRepoConfig.getUri() will include a fingerprint, which may not match with
        // the repos address in the database.
        Repo repo = RepoProvider.Helper.findByAddress(getActivity(), newRepoConfig.getUriString());
        if (repo == null) {
            ContentValues values = new ContentValues(6);

             // TODO: i18n and think about most appropriate name. Although it wont be visible in
             // the "Manage repos" UI after being marked as a swap repo here...
            values.put(RepoProvider.DataColumns.NAME, "Swap");
            values.put(RepoProvider.DataColumns.ADDRESS, newRepoConfig.getUriString());
            values.put(RepoProvider.DataColumns.DESCRIPTION, ""); // TODO;
            values.put(RepoProvider.DataColumns.FINGERPRINT, newRepoConfig.getFingerprint());
            values.put(RepoProvider.DataColumns.IN_USE, true);
            values.put(RepoProvider.DataColumns.IS_SWAP, true);
            Uri uri = RepoProvider.Helper.insert(getActivity(), values);
            repo = RepoProvider.Helper.findByUri(getActivity(), uri);
        } else if (!repo.isSwap) {
            Log.d(TAG, "Old local repo being marked as \"Swap\" repo, so that it wont appear in the list of repositories in the future.");
            ContentValues values = new ContentValues(1);
            values.put(RepoProvider.DataColumns.IS_SWAP, true);
            RepoProvider.Helper.update(getActivity(), repo, values);
        }
        return repo;
    }

    @Override
    public void onProgress(Event event) {
        // TODO: Show progress, but we can worry about that later.
        // Might be nice to have it nicely embedded in the UI, rather than as
        // an additional dialog. E.g. White text on blue, letting the user
        // know what we are up to.

        switch (event.type) {
        case UpdateService.EVENT_COMPLETE_AND_SAME:
        case UpdateService.EVENT_COMPLETE_WITH_CHANGES:
            ((ConnectSwapActivity)getActivity()).onRepoUpdated(repo);
            /*Intent intent = new Intent();
            intent.putExtra("category", newRepoConfig.getHost()); // TODO: Load repo from database to get proper name. This is what the category we want to select will be called.
            getActivity().setResult(Activity.RESULT_OK, intent);
            finish();*/
            break;
        case UpdateService.EVENT_ERROR:
            // TODO: Show message on this screen (with a big "okay" button that goes back to F-Droid activity)
            // rather than finishing directly.
            finish();
            break;
        }
    }
}
