package ru.karaokeplus.karaokeplus.search;

import ru.karaokeplus.karaokeplus.content.data.Song;


public class SongSuggestion {
    public String author;
    public String songname;
    public Song song;

    public SongSuggestion(String author, String song) {
        this.author = author;
        this.songname = song;
    }

    public SongSuggestion(Song song) {
        this.songname = song.getSongName();
        this.author = song.getSongAuthor();
        this.song = song;
    }
}
