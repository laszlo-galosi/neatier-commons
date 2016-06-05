/*
 *  Copyright (C) 2016 Delight Solutions Ltd., All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited.
 *  Proprietary and confidential.
 *
 *  All information contained herein is, and remains the property of Delight Solutions Kft.
 *  The intellectual and technical concepts contained herein are proprietary to Delight Solutions Kft.
 *   and may be covered by U.S. and Foreign Patents, pending patents, and are protected
 *  by trade secret or copyright law. Dissemination of this information or reproduction of
 *  this material is strictly forbidden unless prior written permission is obtained from
 *   Delight Solutions Kft.
 */

package com.neatier.commons.data.caching;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by László Gálosi on 24/07/15
 */
@Singleton
public class FileOnDeviceKeyedStorageImpl<K, V>
        implements OnDeviceKeyedStorage.FileOnDeviceKeyStorage<K, V> {

    private final File directory;
    private final String fileNamePrefix;
    private File mKeyedFile;
    private FilenameFilter mFileNameFilter;

    /**
     * Constructor width a specific file in a specific directory on the device.
     *
     * @param directory the directory containing the specific file.
     * @param prefix    the file name prefix appended by the key will be the file name.
     */
    public FileOnDeviceKeyedStorageImpl(final File directory, final String prefix) {
        this.directory = directory;
        this.fileNamePrefix = prefix;
    }

    /**
     * Overwrites a particular keyed file named with prefix appended by the key, if the
     * file not
     * exists first creates.
     *
     * @param key     the int key or id of the content
     * @param content the content value
     * @throws IOException
     */
    @Override
    public void writeKeyedContent(final K key, final V content) {
        setKeyedFile(key);
        try {
            FileWriter writer = new FileWriter(mKeyedFile, false);
            writer.write(content.toString());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {

        }
    }

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
                fileContentBuilder.append("\n" + stringLine.trim());
            }
            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {

        }
        return (V) fileContentBuilder.toString();
    }

    @Override
    public Observable<String> readAll() {
        if (directory.exists()) {
            File[] files = directory.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(final File dir, final String filename) {
                    return !fileNamePrefix.isEmpty() && filename.contains(fileNamePrefix);
                }
            });
            return Observable.from(files).map(new Func1<File, String>() {
                @Override
                public String call(final File f) {
                    return FileManager.getInstance().readFileContent(f);
                }
            });
        }
        return Observable.empty();
    }

    @Override
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

    @Override
    public void clear() {
        if (directory.exists()) {
            File[] files = directory.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(final File dir, final String filename) {
                    return !fileNamePrefix.isEmpty() && filename.contains(fileNamePrefix);
                }
            });
            for (int i = 0; i < files.length; i++) {
                files[i].delete();
            }
        }
    }

    @Override
    public Observable keys() {
        if (directory.exists()) {
            String[] fileNames = directory.list(new FilenameFilter() {
                @Override
                public boolean accept(final File dir, final String filename) {
                    return !fileNamePrefix.isEmpty() && filename.contains(fileNamePrefix);
                }
            });
            return Observable.from(fileNames).map(new Func1<String, K>() {
                @Override
                public K call(final String fileName) {
                    return (K) extractKeyFromFileName(fileName);
                }
            });
        }
        return Observable.empty();
    }

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
    public Class getKeyClass() {
        return Long.class;
    }

    private void setKeyedFile(final K key) {
        final StringBuilder fileNameBuilder = new StringBuilder();
        fileNameBuilder.append(directory.getPath());
        fileNameBuilder.append(File.separator);
        fileNameBuilder.append(this.fileNamePrefix);
        fileNameBuilder.append(String.format("%d", key));
        mKeyedFile = new File(fileNameBuilder.toString());
    }

    private void setKeyedFileNameFilter(final K key) {
        mFileNameFilter = new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String filename) {
                return dir.equals(directory) && filename.equals(
                        String.format("%s%d", fileNamePrefix, key));
            }
        };
    }
}
