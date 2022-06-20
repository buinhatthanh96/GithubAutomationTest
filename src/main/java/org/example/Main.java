package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class Main {

    private static Object object;

    public static class OpenIssueOfRepo {
        private String repoName;
        private int count;
        private String updatedDate;
        public OpenIssueOfRepo(String _repoName, int _count, String _updatedDate) {
            setRepoName(_repoName);
            setCount(_count);
            setUpdatedDate(_updatedDate);
        }

        public String getRepoName() {
            return repoName;
        }

        public void setRepoName(String repoName) {
            this.repoName = repoName;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        @Override
        public String toString() {
            return String.format("Repo %s is update at %s having %s open issues", getRepoName(), getUpdatedDate(), getCount());
        }

        public String getUpdatedDate() {
            return updatedDate;
        }

        public void setUpdatedDate(String updatedDate) {
            this.updatedDate = updatedDate;
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        var org = "SeleniumHQ";
        var getReposResponse = CallApi(String.format("https://api.github.com/orgs/%s/repos?sort=updated&direction=desc", org));
        var nameOfMostWatcherRepo = new String();
        int maxOfMostWatcherRepo = 0;
        ArrayList<Object> openIssue = new ArrayList<Object>();
        JSONArray repos = new JSONArray(getReposResponse);
        for (int i = 0; i < repos.length(); i++) {
            JSONObject repoInfo = repos.getJSONObject(i);
            var repoName = repoInfo.getString("name");
            var getRepoResponse = CallApi(String.format("https://api.github.com/repos/%s/%s", org, repoName));
            JSONObject repo = new JSONObject(getRepoResponse);
            openIssue.add(new OpenIssueOfRepo(repoName, repo.getInt("open_issues_count"), repo.getString("updated_at")));
            if (repo.getInt("watchers") > maxOfMostWatcherRepo) {
                maxOfMostWatcherRepo = repo.getInt("watchers");
                nameOfMostWatcherRepo = repoName;
            }
        }

        System.out.println(String.format("the list of descending updated date repo with open issue count:"));
        openIssue.forEach(repo -> System.out.println(repo.toString()));
        System.out.println(String.format("%s is repository has the most watchers with %s watcher", nameOfMostWatcherRepo, maxOfMostWatcherRepo));
        return;
    }

    private static String CallApi(String url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Authorization","token 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")
                .build();
        var response =
                client.send(request, HttpResponse.BodyHandlers.ofString());
        return  response.body();
    }
}