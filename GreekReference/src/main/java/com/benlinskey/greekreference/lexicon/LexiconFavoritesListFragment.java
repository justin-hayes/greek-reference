/*
 * Copyright 2013 Benjamin Linskey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.benlinskey.greekreference.lexicon;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.benlinskey.greekreference.R;
import com.benlinskey.greekreference.data.appdata.AppDataContract;
import com.benlinskey.greekreference.data.appdata.LexiconFavoritesProvider;

/**
 * // TODO: Display a message when the favorites list is empty.
 * <p>
 * Boilerplate description:
 * A list fragment representing a list of Items. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link LexiconDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class LexiconFavoritesListFragment extends LexiconListFragment
        implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String NAME = "lexicon_favorites";
    SimpleCursorAdapter mAdapter;

    // Note that we have to select the lexicon ID in order to alphabetize; alphabetizing by word
    // doesn't work due to accented characters.
    private static final String[] PROJECTION = new String[] {AppDataContract.LexiconFavorites._ID,
            AppDataContract.LexiconFavorites.COLUMN_NAME_WORD,
            AppDataContract.LexiconFavorites.COLUMN_NAME_LEXICON_ID};
    private static final String SELECTION = "";
    private static final String[] SELECTION_ARGS = {};
    private static final String SORT_ORDER
            = AppDataContract.LexiconFavorites.COLUMN_NAME_LEXICON_ID + " ASC";

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String fragmentName, int id) {
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LexiconFavoritesListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create and set list adapter.
        // TODO: Display a message when this list is empty.
        String[] fromColumns = {AppDataContract.LexiconFavorites.COLUMN_NAME_WORD};
        int[] toViews = {android.R.id.text1};
        mAdapter = new android.widget.SimpleCursorAdapter(getActivity(),
                R.layout.greek_simple_list_item_activated_1, null, fromColumns, toViews, 0);
        setListAdapter(mAdapter);

        getLoaderManager().initLoader(0, null, this);
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), LexiconFavoritesProvider.CONTENT_URI, PROJECTION, SELECTION,
                SELECTION_ARGS, SORT_ORDER);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        Cursor cursor = (Cursor) mAdapter.getItem(position);
        int lexiconFavoritesId = cursor.getInt(0);
        setSelectedLexiconItemId(lexiconFavoritesId);
        mCallbacks.onItemSelected(NAME, lexiconFavoritesId);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    private void setSelectedLexiconItemId(int id) {
        String[] columns = new String[] {AppDataContract.LexiconFavorites.COLUMN_NAME_LEXICON_ID};
        String selection = AppDataContract.LexiconFavorites._ID + " = ?";
        String[] selectionArgs = new String[] {Integer.toString(id)};
        Cursor cursor = getActivity().getContentResolver()
                .query(AppDataContract.LexiconFavorites.CONTENT_URI, columns, selection,
                        selectionArgs, null);

        if (cursor.moveToFirst()) {
            mSelectedLexiconId = cursor.getInt(0);
        } else {
            throw new IllegalArgumentException("Invalid ID: " + id);
        }
    }
}
