package ru.karaokeplus.karaokeplus;


import android.os.Environment;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ru.karaokeplus.karaokeplus.content.data.CategoryContent;
import ru.karaokeplus.karaokeplus.content.data.Song;

public class Utils {

    public static List<Song> readFileFromStorage() throws IOException {

        List<Song> ret = new ArrayList<Song>();
        String filename = "songs.txt";
        File myExternalFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), filename);

        FileInputStream fis = new FileInputStream(myExternalFile);
        DataInputStream in = new DataInputStream(fis);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String strLine;

        while ((strLine = br.readLine()) != null) {

            String [] items = strLine.split("_");

            if(items.length == 4) {
                Song song = new Song();
                song.setSongAuthor(items[0]);
                song.setSongName(items[1]);
                song.setSongCategories(items[2]);
                song.setSongCode(items[3]);

                ret.add(song);
            }
        }
        in.close();

        return ret;
    }

    public static List<Song> filterByCategory(List<Song> songs,CategoryContent.CategoryItem category) {
        List<Song> ret = new ArrayList<>();

        for(Song song : ((List<Song>)songs)) {

            if(song.getCategories().contains(category)) {
                ret.add(song);
            }
        }

        return ret;
    }
}
