package com.t0p47.mendeley.model;

/**
 * Created by 01 on 08.10.2017.
 */

public class JournalArticle {

    int local_id;
    int global_id;
    String title;
    String authors;
    String abstractField;
    int journal_id;
    int volume;
    int issue;
    int year;
    int pages;
    int ArXivID;
    int DOI;
    int PMID;
    int folder;
    int filepath;

    public JournalArticle() {}

    public JournalArticle(int local_id, int global_id, String title, String authors, String abstractField, int journal_id, int volume, int issue, int year, int pages, int arXivID, int DOI, int PMID, int folder, int filepath) {
        this.local_id = local_id;
        this.global_id = global_id;
        this.title = title;
        this.authors = authors;
        this.abstractField = abstractField;
        this.journal_id = journal_id;
        this.volume = volume;
        this.issue = issue;
        this.year = year;
        this.pages = pages;
        ArXivID = arXivID;
        this.DOI = DOI;
        this.PMID = PMID;
        this.folder = folder;
        this.filepath = filepath;
    }

    public int getLocal_id() {
        return local_id;
    }

    public void setLocal_id(int local_id) {
        this.local_id = local_id;
    }

    public int getGlobal_id() {
        return global_id;
    }

    public void setGlobal_id(int global_id) {
        this.global_id = global_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getAbstractField() {
        return abstractField;
    }

    public void setAbstractField(String abstractField) {
        this.abstractField = abstractField;
    }

    public int getJournal_id() {
        return journal_id;
    }

    public void setJournal_id(int journal_id) {
        this.journal_id = journal_id;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getIssue() {
        return issue;
    }

    public void setIssue(int issue) {
        this.issue = issue;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getArXivID() {
        return ArXivID;
    }

    public void setArXivID(int arXivID) {
        ArXivID = arXivID;
    }

    public int getDOI() {
        return DOI;
    }

    public void setDOI(int DOI) {
        this.DOI = DOI;
    }

    public int getPMID() {
        return PMID;
    }

    public void setPMID(int PMID) {
        this.PMID = PMID;
    }

    public int getFolder() {
        return folder;
    }

    public void setFolder(int folder) {
        this.folder = folder;
    }

    public int getFilepath() {
        return filepath;
    }

    public void setFilepath(int filepath) {
        this.filepath = filepath;
    }
}
