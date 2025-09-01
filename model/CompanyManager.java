package model;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class CompanyManager {
	private List<Company> companies = new ArrayList<>();
	private String fileName;

	public CompanyManager(String fileName) {
		this.fileName = fileName;
		loadFromFile();
	}

	public void loadFromFile() {
		companies.clear();
		try {
			List<String> lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
			for (String line : lines) {
				String[] parts = line.split("=", -1);
				if (parts.length >= 4) {
					companies.add(new Company(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim()));
				}
			}
		} catch (IOException e) {
			System.err.println("Error reading CSV: " + e.getMessage());
		}
	}

	public void saveToFile() {
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName), StandardCharsets.UTF_8)) {
			for (Company c : companies) {
				writer.write(String.join("=", c.getOldName(), c.getNewName(), c.getRegister(), c.getTrId()));
				writer.newLine();
			}
		} catch (IOException e) {
			System.err.println("Recording error CSV: " + e.getMessage());
		}
	}

	 /** Normalizing a string for search */
    private String normalize(String s) {
        return s.toLowerCase()
                .replaceAll("[^a-z0-9 ]", "") // leave only letters/numbers/spaces
                .replaceAll("\\s+", " ")      // multiple spaces -> one
                .trim();
    }

    /**Smart Search: Normalization + contains + similarity */
    public List<Company> searchSmart(String query) {
        List<Company> results = new ArrayList<>();
        String normQuery = normalize(query);

        for (Company c : companies) {
            String normOld = normalize(c.getOldName());

            // 1. Exact match
            if (normOld.equals(normQuery)) {
                results.add(c);
                continue;
            }

            // 2. Partial match
            if (normOld.contains(normQuery)) {
                results.add(c);
                continue;
            }

            // 3. Similarity (softer threshold)
            double sim = similarity(normQuery, normOld);
            if (sim >= 0.7) {
                results.add(c);
            }
        }

        // Sort by similarity (best on top)
        results.sort((c1, c2) -> {
            double s1 = similarity(normQuery, normalize(c1.getOldName()));
            double s2 = similarity(normQuery, normalize(c2.getOldName()));
            return Double.compare(s2, s1);
        });

        return results;
    }

    /**The old method now redirects to searchSmart */
    public List<Company> searchByOldName(String oldName) {
        return searchSmart(oldName);
    }

    public List<Company> searchByOldNameContains(String part) {
        List<Company> results = new ArrayList<>();
        String lower = part.toLowerCase();
        for (Company c : companies) {
            if (c.getOldName().toLowerCase().contains(lower)) {
                results.add(c);
            }
        }
        return results;
    }

    /**Basic search with fallback */
    public List<Company> searchWithFallback(String query) {
        return searchSmart(query);
    }

    /**Search by similarity  (Levenshtein-based) */
    private List<Company> searchByOldNameSimilar(String query, double threshold) {
        List<Company> results = new ArrayList<>();
        String qLower = query.toLowerCase();

        for (Company c : companies) {
            double sim = similarity(qLower, c.getOldName().toLowerCase());
            if (sim >= threshold) {
                results.add(c);
            }
        }
        return results;
    }

    /** Similarity of strings based on Levenshtein */
    private double similarity(String s1, String s2) {
        int maxLen = Math.max(s1.length(), s2.length());
        if (maxLen == 0)
            return 1.0;
        int dist = levenshteinDistance(s1, s2);
        return 1.0 - ((double) dist / maxLen);
    }

    /** Levenshtein distance */
    private int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++)
            dp[i][0] = i;
        for (int j = 0; j <= s2.length(); j++)
            dp[0][j] = j;

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }
        return dp[s1.length()][s2.length()];
    }

	public void addCompany(Company company) {
		companies.add(company);
		saveToFile();
	}

	public boolean editCompany(String oldName, Company newData) {
		for (int i = 0; i < companies.size(); i++) {
			if (companies.get(i).getOldName().equalsIgnoreCase(oldName)) {
				companies.set(i, newData);
				saveToFile();
				return true;
			}
		}
		return false;
	}

	public boolean deleteCompany(String oldName) {
		boolean removed = companies.removeIf(c -> c.getOldName().equalsIgnoreCase(oldName));
		if (removed) {
			saveToFile();
		}
		return removed;
	}

	public List<Company> getAllCompanies() {
		return companies;
	}
}
