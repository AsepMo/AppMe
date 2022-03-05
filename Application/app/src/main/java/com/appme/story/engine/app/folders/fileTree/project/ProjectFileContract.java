package com.appme.story.engine.app.folders.fileTree.project;

import java.io.File;

public class ProjectFileContract {
    public interface View {
        void display(File projectFile, boolean expand);

        void refresh();

        void setPresenter(Presenter presenter);
    }

    public interface Presenter {
        void show(File projectFile, boolean expand);

        void refresh(File projectFile);
    }




}

