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
    String journal;
    int volume;
    int issue;
    int year;
    int pages;
    int ArXivID;
    int DOI;
    int PMID;
    int folder;
    String filePath;
    String created_at;
    String updated_at;
    int favorite;

    public JournalArticle() {}

    public JournalArticle(int local_id, int global_id, String title, String authors, String abstractField, String journal, int volume, int issue, int year, int pages, int arXivID, int DOI, int PMID, int folder, String filePath, String created_at, String updated_at, int favorite) {
        this.local_id = local_id;
        this.global_id = global_id;
        this.title = title;
        this.authors = authors;
        this.abstractField = abstractField;
        this.journal = journal;
        this.volume = volume;
        this.issue = issue;
        this.year = year;
        this.pages = pages;
        ArXivID = arXivID;
        this.DOI = DOI;
        this.PMID = PMID;
        this.folder = folder;
        this.filePath = filePath;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.favorite = favorite;
    }


    public JournalArticle(int local_id, String title, String authors, String journal, String created_at, int favorite, String filePath) {
        this.local_id = local_id;
        this.title = title;
        this.authors = authors;
        this.journal = journal;
        this.filePath = filePath;
        this.created_at = created_at;
        this.favorite = favorite;
    }

    public JournalArticle(int local_id, String title, String authors, String abstractField, String journal, int volume,
                          int issue, int pages, int year, int ArXivID, int DOI, int PMID, String created_at, int favorite, String filePath) {
        this.local_id = local_id;
        this.title = title;
        this.authors = authors;
        this.journal = journal;
        this.abstractField = abstractField;
        this.filePath = filePath;
        this.created_at = created_at;
        this.favorite = favorite;
        this.volume = volume;
        this.issue = issue;
        this.year = year;
        this.pages = pages;
        this.ArXivID = ArXivID;
        this.DOI = DOI;
        this.PMID = PMID;
    }

    //response [{"id":109,"title":"ForTask2","authors":"Alex","abstract":null,"journal_id":"01","volume":null,
    // "issue":null,"year":null,"pages":null,"ArXivID":null,"DOI":null,"PMID":null,"folder":2,
    // "filePath":null,"1":0,"uid":19,"created_at":"2017-09-28 06:57:34",
    // "updated_at":"2017-09-28 06:57:34","delete_date":"0000-00-00 00:00:00","favorite":1}


    public JournalArticle(int global_id, String title, String authors, String abstractField, String journal, int volume, int issue, int year, int pages, int arXivID, int DOI, int PMID, int folder, String filePath, String created_at, String updated_at, int favorite) {
        this.global_id = global_id;
        this.title = title;
        this.authors = authors;
        this.abstractField = abstractField;
        this.journal = journal;
        this.volume = volume;
        this.issue = issue;
        this.year = year;
        this.pages = pages;
        ArXivID = arXivID;
        this.DOI = DOI;
        this.PMID = PMID;
        this.folder = folder;
        this.filePath = filePath;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.favorite = favorite;
    }

    //For local create
    public JournalArticle(String title, String authors, String abstractField, String journal, int volume, int issue, int year, int pages, int arXivID, int DOI, int PMID, String filePath) {
        this.title = title;
        this.authors = authors;
        this.abstractField = abstractField;
        this.journal = journal;
        this.volume = volume;
        this.issue = issue;
        this.year = year;
        this.pages = pages;
        ArXivID = arXivID;
        this.DOI = DOI;
        this.PMID = PMID;
        this.filePath = filePath;
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

    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }
}
