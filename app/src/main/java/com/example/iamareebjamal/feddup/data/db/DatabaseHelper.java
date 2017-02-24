package com.example.iamareebjamal.feddup.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.iamareebjamal.feddup.api.PostService;
import com.example.iamareebjamal.feddup.data.db.schema.DraftColumns;

import rx.Observable;

public class DatabaseHelper {

    private Context context;

    public DatabaseHelper(Context context) {
        this.context = context;
    }

    public Observable<PostService> getDrafts() {
        return Observable.create(subscriber -> {
            Cursor cursor = context.getContentResolver().query(
                    DatabaseProvider.Drafts.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);

            if (cursor != null) {
                cursor.moveToFirst();

                do {

                    String title = cursor.getString(cursor.getColumnIndex(DraftColumns.title));
                    String author = cursor.getString(cursor.getColumnIndex(DraftColumns.author));
                    String content = cursor.getString(cursor.getColumnIndex(DraftColumns.content));
                    String filePath = cursor.getString(cursor.getColumnIndex(DraftColumns.filePath));

                    PostService postService = new PostService();
                    postService.setTitle(title);
                    postService.setAuthor(author);
                    postService.setContent(content);
                    postService.setFilePath(filePath);

                    subscriber.onNext(postService);
                } while (cursor.moveToNext());

                cursor.close();
            }

            subscriber.onCompleted();
        });
    }

    public Observable<Uri> insertDraft(PostService postService) {

        return Observable.create(subscriber -> {
            ContentValues values = new ContentValues();
            values.put(DraftColumns.title, postService.getTitle());
            values.put(DraftColumns.author, postService.getAuthor());
            values.put(DraftColumns.content, postService.getContent());
            values.put(DraftColumns.filePath, postService.getFilePath());
            Uri uri = context.getContentResolver().insert(DatabaseProvider.Drafts.CONTENT_URI, values);

            subscriber.onNext(uri);
            subscriber.onCompleted();
        });
    }

    public Observable<Integer> updateDraft(Uri draftUri, PostService postService) {
        return Observable.create(subscriber -> {
            ContentValues values = new ContentValues();
            values.put(DraftColumns.title, postService.getTitle());
            values.put(DraftColumns.author, postService.getAuthor());
            values.put(DraftColumns.content, postService.getContent());
            values.put(DraftColumns.filePath, postService.getFilePath());

            int rows = context.getContentResolver().update(draftUri, values, null, null);

            subscriber.onNext(rows);
            subscriber.onCompleted();
        });
    }

    public Observable<Integer> deleteUri(Uri uri) {
        return Observable.create(subscriber -> {

            int rows = context.getContentResolver().delete(uri, null, null);

            subscriber.onNext(rows);
            subscriber.onCompleted();
        });
    }
}
