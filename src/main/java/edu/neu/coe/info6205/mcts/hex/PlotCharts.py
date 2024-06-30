#!/usr/bin/env python3

# python3 -m pip install -U pandas seaborn matplotlib
import os
import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt

# Benchmark CSV File
bestNodeCsvFile = '/Users/suchitadabir/NEU_MS/github_repos/INFO6205_PROJECT/bestNode.csv'
mctsRunCsvFile = '/Users/suchitadabir/NEU_MS/github_repos/INFO6205_PROJECT/mctsRunTime.csv'
file_dir = os.path.dirname(bestNodeCsvFile)

# Read the CSV file into a DataFrame
df_bn = pd.read_csv(bestNodeCsvFile)
df_bn["winRatio"] = df_bn["wins"] / df_bn["playouts"]

# Create a seaborn plot
plt.figure()
sns.lineplot(data=df_bn, x='numRuns', y='winRatio', hue='explorationFactor', marker='o', errorbar=None, palette="tab10")

# Adding titles and labels
plt.title('Number of Runs vs. Winning Ratio')
plt.xlabel('Number of Runs')
plt.ylabel('# Wins / # Playouts')
plt.legend(title='Exploration Factors')
plt.grid()

# Display the plot
plt_filename = os.path.join(file_dir, "wrnr.pdf")
plt.savefig(plt_filename, bbox_inches='tight')
plt.show()
plt.close()

# Create a seaborn plot
plt.figure()
sns.lineplot(data=df_bn, x='explorationFactor', y='winRatio', hue='numRuns', marker='o', errorbar=None, palette="tab10")

# Adding titles and labels
plt.title('Exploration Factors vs. Winning Ratio')
plt.xlabel('Exploration Factors')
plt.ylabel('# Wins / # Playouts')
plt.legend(title='Number of Runs')
plt.grid()

# Display the plot
plt_filename = os.path.join(file_dir, "wref.pdf")
plt.savefig(plt_filename, bbox_inches='tight')
plt.show()
plt.close()


# Read the CSV file into a DataFrame
df_mr = pd.read_csv(mctsRunCsvFile)
df_mr["totalTime"] = df_mr["explore_time"] + df_mr["select_time"] + df_mr["sim_time"] + df_mr["bp_time"]

time_columns = ["explore_time", "select_time", "sim_time", "bp_time", "totalTime"]
time_columns_labels = ["Exploration Time", "Selection Time", "Simulation Time", "Back-propagation Time", "Total Time"]

for i, tc in enumerate(time_columns):

    # Create a seaborn plot
    plt.figure()
    sns.lineplot(data=df_mr, x='numRuns', y=tc, hue='explorationFactor', marker='o', errorbar=None, palette="tab10")

    # Adding titles and labels
    plt.title('Number of Runs vs. {}'.format(time_columns_labels[i]))
    plt.xlabel('Number of Runs')
    plt.ylabel(time_columns_labels[i])
    plt.legend(title='Exploration Factors')
    plt.grid()

    # Display the plot
    plt_filename = os.path.join(file_dir, "{}_nr.pdf".format(tc))
    plt.savefig(plt_filename, bbox_inches='tight')
    plt.show()
    plt.close()

    # Create a seaborn plot
    plt.figure()
    sns.lineplot(data=df_mr, x='explorationFactor', y=tc, hue='numRuns', marker='o', errorbar=None, palette="tab10")

    # Adding titles and labels
    plt.title('Exploration Factors vs {}'.format(time_columns_labels[i]))
    plt.xlabel('Exploration Factors')
    plt.ylabel(time_columns_labels[i])
    plt.legend(title='Number of Runs')
    plt.grid()

    # Display the plot
    plt_filename = os.path.join(file_dir, "{}_ef.pdf".format(tc))
    plt.savefig(plt_filename, bbox_inches='tight')
    plt.show()
    plt.close()


# Read the CSV file into a DataFrame
df_mr_fixed_nr = df_mr.loc[df_mr["numRuns"] == 128]
df_mr_fixed_ef = df_mr.loc[df_mr["explorationFactor"] == 1.14]

for i, tc in enumerate(time_columns):

    # Create a seaborn plot
    plt.figure()
    sns.barplot(data=df_mr_fixed_nr, x='explorationFactor', y=tc,  errorbar=None, palette="tab10")

    # Adding titles and labels
    plt.title('Exploration Factors vs. {}'.format(time_columns_labels[i]))
    plt.xlabel('Exploration Factors')
    plt.ylabel(time_columns_labels[i])
    plt.legend()

    # Display the plot
    plt_filename = os.path.join(file_dir, "{}_nr_128.pdf".format(tc))
    plt.savefig(plt_filename, bbox_inches='tight')
    plt.show()
    plt.close()

    # Create a seaborn plot
    plt.figure()
    sns.barplot(data=df_mr_fixed_ef, x='numRuns', y=tc, errorbar=None, palette="tab10")

    # Adding titles and labels
    plt.title('Number of Runs vs {}'.format(time_columns_labels[i]))
    plt.xlabel('Number of Runs')
    plt.ylabel(time_columns_labels[i])
    plt.legend()

    # Display the plot
    plt_filename = os.path.join(file_dir, "{}_ef_root2.pdf".format(tc))
    plt.savefig(plt_filename, bbox_inches='tight')
    plt.show()
    plt.close()
