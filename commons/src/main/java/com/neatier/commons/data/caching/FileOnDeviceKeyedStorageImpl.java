/*
 *  Copyright (C) 2016 Delight Solutions Ltd., All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited.
 *  Proprietary and confidential.
 *
 *  All information contained herein is, and remains the property of Delight Solutions Kft.
 *  The intellectual and technical concepts contained herein are proprietary to Delight Solutions
  *  Kft.
 *   and may be covered by U.S. and Foreign Patents, pending patents, and are protected
 *  by trade secret or copyright law. Dissemination of this information or reproduction of
 *  this material is strictly forbidden unless prior written permission is obtained from
 *   Delight Solutions Kft.
 */

package com.neatier.commons.data.caching;

import android.support.annotation.NonNull;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import javax.inject.Singleton;
import rx.Observable;

/**
 * FileOnDeviceKeyStorage implementation to store key-value pairs in a {@link File}
 * locally on the device.
 *
 * @author László Gálosi
 * @since 24/07/15
 */
@Singleton
public abstract class FileOnDeviceKeyedStorageImpl<K, V>
        implements OnDeviceKeyedStorage.FileOnDeviceKeyStorage<K, V> {

    private final File directory;
    private final String fileNamePrefix;
    private File mKeyedFile;
    private FilenameFilter mFileNameFilter;

    /**
     * Constructor width the given file in the given directory on the device.
     *
     * @param directory the directory containing the specific file.
     * @param prefix the file name prefix appended by the key will be the file name.
     */
    public FileOnDeviceKeyedStorageImpl(final File directory, final String prefix) {
        this.directory = directory;
        this.fileNamePrefix = prefix;
    }

    /**
     * Overwrites a particular keyed file named with prefix appended by the key. It creates the file
     * if it is not exists.
     *
     * @param key the int key or id of the content
     * @param content the content value
     */
    @Override
    public void writeKeyedContent(final K key, @NonNull final V content) {
        setKeyedFile(key);
        try {
            FileWriter writer = new FileWriter(mKeyedFile, false);
            writer.write(content.toString());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public V readOneByKey(final K key) {
        StringBuilder fileContentBuilder = new StringBuilder();
        if (!containsKey(key)) {
            setKeyedFile(key);
        }
        String stringLine;
        try {
            FileReader fileReader = new FileReader(this.mKeyedFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            stringLine = bufferedReader.readLine();
            fileContentBuilder.append(stringLine.trim());
            while ((stringLine = bufferedReader.readLine()) != null) {
                fileContentBuilder.append("\n").append(stringLine.trim());
            }
            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return (V) fileContentBuilder.toString();
    }

    @Override
    public Observable<String> readAll() {
        if (directory.exists()) {
            File[] files = directory.listFiles(
                    (dir, filename) -> !fileNamePrefix.isEmpty() && filename.contains(
                            fileNamePrefix));
            return Observable.from(files).map(f -> FileManager.getInstance().readFileContent(f));
        }
        return Observable.empty();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored") @Override
    public void removeOneByKey(final K key) {
        setKeyedFile(key);
        if (containsKey(key)) {
            this.mKeyedFile.delete();
        }
    }

    @Override
    public boolean containsKey(final K key) {
        setKeyedFileNameFilter(key);
        String[] resultFiles = directory.list(mFileNameFilter);
        return resultFiles != null && resultFiles.length == 1;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored") @Override
    public void clear() {
        if (directory.exists()) {
            File[] files = directory.listFiles(
                    (dir, filename) -> !fileNamePrefix.isEmpty() && filename.contains(
                            fileNamePrefix));
            for (final File file : files) {
                file.delete();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Observable keys() {
        if (directory.exists()) {
            String[] fileNames = directory.list(
                    (dir, filename) -> !fileNamePrefix.isEmpty() && filename.contains(
                            fileNamePrefix));
            return Observable.from(fileNames).map(fileName -> (K) extractKeyFromFileName(fileName));
        }
        return Observable.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object extractKeyFromFileName(final String fileName) {
        String keyPart = fileName.substring(fileNamePrefix.length());
        Class keyClass = getKeyClass();
        if (keyClass == Long.class) {
            return Long.parseLong(keyPart);
        } else if (keyClass == Integer.class) {
            return Integer.parseInt(keyPart);
        } else if (keyClass == String.class) {
            return keyPart;
        }
        return fileNamePrefix;
    }

    @Override
    public abstract Class<K> getKeyClass();

    private void setKeyedFile(final K key) {
        final String fileNameBuilder = directory.getPath() +
                File.separator +
                this.fileNamePrefix +
                String.format("%s", key.toString());
        mKeyedFile = new File(fileNameBuilder);
    }

    private void setKeyedFileNameFilter(final K key) {
        mFileNameFilter = (dir, filename) -> dir.equals(directory) && filename.equals(
                String.format("%s%s", fileNamePrefix, key.toString()));
    }
}
