package it.jaschke.alexandria;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.services.DownloadImage;


public class BookDetail extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EAN_KEY = "EAN";
    private final int LOADER_ID = 10;
    private View rootView;
    private String ean;
    private String bookTitle;
    private ShareActionProvider shareActionProvider;

    public BookDetail(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            ean = arguments.getString(BookDetail.EAN_KEY);
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }

        rootView = inflater.inflate(R.layout.fragment_full_book, container, false);
        rootView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, ean);
                bookIntent.setAction(BookService.DELETE_BOOK);
                getActivity().startService(bookIntent);
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.book_detail, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(createShareIntent());
        }
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(ean)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        /**
         * Extra credits problem #2.2
         * Manage empty data
         */

        bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        if(bookTitle!=null) {
            ((TextView) rootView.findViewById(R.id.fullBookTitle)).setText(bookTitle);
        } else {
            ((TextView) rootView.findViewById(R.id.fullBookTitle)).setText(R.string.no_title);
        }

        setShareIntent(createShareIntent());

        String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        if(bookSubTitle!=null) {
            ((TextView) rootView.findViewById(R.id.fullBookSubTitle)).setText(bookSubTitle);
        }

        String desc = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.DESC));
        if(desc!=null) {
            ((TextView) rootView.findViewById(R.id.fullBookDesc)).setText(desc);
        } else {
            ((TextView) rootView.findViewById(R.id.fullBookDesc)).setText(getString(R.string.no_desc));
        }

        String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        if(authors!=null) {
            String[] authorsArr = authors.split(",");
            ((TextView) rootView.findViewById(R.id.authors)).setLines(authorsArr.length);
            ((TextView) rootView.findViewById(R.id.authors)).setText(authors.replace(",", "\n"));
        } else {
            ((TextView) rootView.findViewById(R.id.authors)).setText(getString(R.string.no_authors));
        }

        String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        if(imgUrl!=null) {
            if (Patterns.WEB_URL.matcher(imgUrl).matches()) {
                if(Utility.isNetworkAvailable(getActivity())) {
                    new DownloadImage((ImageView) rootView.findViewById(R.id.fullBookCover)).execute(imgUrl);
                    rootView.findViewById(R.id.fullBookCover).setVisibility(View.VISIBLE);
                }
            }
        }

        String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        if(categories!=null) {
            ((TextView) rootView.findViewById(R.id.categories)).setText(categories);
        } else {
            ((TextView) rootView.findViewById(R.id.categories)).setText(getString(R.string.no_categories));
        }

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    @Override
    public void onPause() {
        super.onDestroyView();
        if(MainActivity.IS_TABLET && rootView.findViewById(R.id.right_container)==null){
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text)+bookTitle);
        return shareIntent;
    }

    // Call to update the share intent
    public void setShareIntent(Intent shareIntent) {
        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(shareIntent);
        }
    }
}