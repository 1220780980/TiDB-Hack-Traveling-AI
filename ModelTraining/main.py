import pandas as pd
import numpy as np
import json
from sklearn.model_selection import train_test_split

attractionInfo = pd.read_csv("data/AttractionInfo.csv")
detailedPlan = pd.read_csv("data/DetailedPlan.csv")
transportationDetail = pd.read_csv("data/TransportationDetail.csv")
trainingData = pd.read_csv("data/TrainingData.csv")
attractionInfo["total_occurrence"] = np.nan
transportationDetail["neighbour_times"] = np.nan


def findTotalOccurrence(attraction):
    count = 0
    for i, v in enumerate(detailedPlan["plan"]):
        plan = json.loads(v)
        keys = plan.keys()
        for k in keys:
            if attraction in plan[k]:
                count += 1

    return count


def countNeighbourOccurrence(attraction1, attraction2):
    count = 0
    for i, v in enumerate(detailedPlan["plan"]):
        plan = json.loads(v)
        keys = plan.keys()
        for k in keys:
            if (attraction1 in plan[k]) and (attraction2 in plan[k]):
                if (plan[k].index(attraction1) == plan[k].index(attraction2) + 1) or \
                        (plan[k].index(attraction1) == plan[k].index(attraction2) - 1):
                    count += 1

    return count


for index, value in enumerate(attractionInfo["total_occurrence"]):
    attractionInfo.loc[index, "total_occurrence"] = findTotalOccurrence(attractionInfo.loc[index, "num"])

for index, value in enumerate(transportationDetail["neighbour_times"]):
    transportationDetail.loc[index, "neighbour_times"] = countNeighbourOccurrence(transportationDetail.loc[index, "attraction1"],
                                                                                  transportationDetail.loc[index, "attraction2"])

attractionInfo["total_occurrence"] = attractionInfo["total_occurrence"].astype("Int64")
transportationDetail["neighbour_times"] = transportationDetail["neighbour_times"].astype("Int64")


def findTransportationTime(attraction1, attraction2):
    condition1 = (transportationDetail['attraction1'] == attraction1) & (transportationDetail['attraction2'] == attraction2)
    condition2 = (transportationDetail['attraction1'] == attraction2) & (transportationDetail['attraction2'] == attraction1)

    time1 = transportationDetail.loc[condition1, 'time']
    time2 = transportationDetail.loc[condition2, 'time']

    if not time1.empty:
        return time1.values[0]
    elif not time2.empty:
        return time2.values[0]
    else:
        return None


def linear_scaling(column):
    min_val = column.min()
    max_val = column.max()
    scaled_column = 1 + (column - min_val) * 9 / (max_val - min_val)
    rounded_column = scaled_column.round().astype(int)
    return rounded_column


for i in range(36):
    for j in range(36):
        if i != j:
            index = i * 36 + j
            totalOccurrence = findTotalOccurrence(i + 1)
            neighbourOccurrence = countNeighbourOccurrence(i + 1, j + 1) / totalOccurrence
            transportationTime = findTransportationTime(i + 1, j + 1)
            score = totalOccurrence / len(detailedPlan) + 0.5 * neighbourOccurrence + 0.5 * 1 / transportationTime
            trainingData.loc[index] = pd.Series([totalOccurrence / len(detailedPlan), neighbourOccurrence, transportationTime, score],
                                                index=trainingData.columns)

trainingData["transportation_time"] = trainingData["transportation_time"].astype("Int64")
trainingData["score"] = linear_scaling(trainingData["score"])

X = trainingData.iloc[:, :-1]
y = trainingData.iloc[:, -1]

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.15, random_state=42)

# 打印划分后的数据集大小
print("训练集大小:", X_train.shape)
print("测试集大小:", X_test.shape)

# pd.set_option('display.max_columns', None)
# pd.set_option('display.max_rows', None)
# pd.set_option('display.width', None)
#
# print(trainingData)
