import pandas as pd
import numpy as np
import json
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LogisticRegression
from sklearn.tree import DecisionTreeClassifier
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score
from sklearn.neighbors import KNeighborsClassifier
from sklearn.svm import SVC
import joblib
from sklearn2pmml import PMMLPipeline, sklearn2pmml

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

X_train, X_valid, y_train, y_valid = train_test_split(X, y, test_size=0.15, random_state=42)

# logistic regression
model = LogisticRegression(max_iter=3000, multi_class="multinomial", solver="lbfgs")
model.fit(X_train, y_train)
filename = "models/logistic_regression_model.joblib"
joblib.dump(model, filename)

# pipeline = PMMLPipeline([("model", LogisticRegression(max_iter=3000, multi_class="multinomial", solver="lbfgs"))])
# pipeline.fit(X_train, y_train)
# filename = "models/logistic_regression_model.pmml"
# sklearn2pmml(pipeline, filename)
# print(1)

# decision tree
depths = [3, 5, 7, 9, 11]

best_depth = None
best_accuracy = 0.0

for depth in depths:
    model = DecisionTreeClassifier(max_depth=depth)
    model.fit(X_train, y_train)
    y_pred = model.predict(X_valid)
    accuracy = accuracy_score(y_valid, y_pred)

    if accuracy > best_accuracy:
        best_accuracy = accuracy
        best_depth = depth

best_model = DecisionTreeClassifier(max_depth=best_depth)
best_model.fit(X_train, y_train)
filename = "models/decision_tree_model.joblib"
joblib.dump(best_model, filename)

# pipeline = PMMLPipeline([("model", DecisionTreeClassifier(max_depth=best_depth))])
# pipeline.fit(X_train, y_train)
# filename = "models/decision_tree_model.pmml"
# sklearn2pmml(pipeline, filename, with_repr=True)
# print(1)

# random forest
param_grid = {
    'n_estimators': [10, 20, 50, 100, 200, 300],
    'max_depth': [None, 5, 10, 15],
    'min_samples_split': [2, 5, 10],
    'min_samples_leaf': [1, 2, 4]
}
model = RandomForestClassifier()
best_params = None
best_accuracy = 0.0

for n_estimators in param_grid['n_estimators']:
    for max_depth in param_grid['max_depth']:
        for min_samples_split in param_grid['min_samples_split']:
            for min_samples_leaf in param_grid['min_samples_leaf']:
                model.set_params(n_estimators=n_estimators, max_depth=max_depth,
                                 min_samples_split=min_samples_split,
                                 min_samples_leaf=min_samples_leaf)
                model.fit(X_train, y_train)
                y_pred = model.predict(X_valid)
                accuracy = accuracy_score(y_valid, y_pred)

                if accuracy > best_accuracy:
                    best_accuracy = accuracy
                    best_params = {
                        'n_estimators': n_estimators,
                        'max_depth': max_depth,
                        'min_samples_split': min_samples_split,
                        'min_samples_leaf': min_samples_leaf
                    }

best_model = RandomForestClassifier(**best_params)
best_model.fit(X_train, y_train)
filename = "models/random_forest_model.joblib"
joblib.dump(best_model, filename)

pipeline = PMMLPipeline([("model", RandomForestClassifier(**best_params))])
pipeline.fit(X_train, y_train)
filename = "models/random_forest_model.pmml"
# sklearn2pmml(pipeline, filename, with_repr=True)
# print(pipeline.predict(X_valid))
# print(1)

# knn
n_neighbors_values = [3, 5, 7, 9, 11]
best_accuracy = 0.0
best_n_neighbors = None

for n_neighbors in n_neighbors_values:
    knn_classifier = KNeighborsClassifier(n_neighbors=n_neighbors)
    knn_classifier.fit(X_train, y_train)
    y_pred = knn_classifier.predict(X_valid)
    accuracy = accuracy_score(y_valid, y_pred)

    if accuracy > best_accuracy:
        best_accuracy = accuracy
        best_n_neighbors = n_neighbors

best_knn_classifier = KNeighborsClassifier(n_neighbors=best_n_neighbors)
best_knn_classifier.fit(X_train, y_train)
filename = "models/knn_model.joblib"
joblib.dump(best_knn_classifier, filename)

pipeline = PMMLPipeline([("model", KNeighborsClassifier(n_neighbors=best_n_neighbors))])
pipeline.fit(X_train, y_train)
filename = "models/knn_model.pmml"
# sklearn2pmml(pipeline, filename, with_repr=True)
# print(pipeline.predict(X_valid))
# print(1)

# svm
C_values = [0.1, 1, 10]
kernel_values = ['linear', 'rbf']
best_accuracy = 0.0
best_C = None
best_kernel = None

for C in C_values:
    for kernel in kernel_values:
        svm_classifier = SVC(C=C, kernel=kernel)
        svm_classifier.fit(X_train, y_train)
        y_pred = svm_classifier.predict(X_valid)
        accuracy = accuracy_score(y_valid, y_pred)

        if accuracy > best_accuracy:
            best_accuracy = accuracy
            best_C = C
            best_kernel = kernel

best_svm_classifier = SVC(C=best_C, kernel=best_kernel)
best_svm_classifier.fit(X_train, y_train)
filename = "models/svm_model.joblib"
joblib.dump(best_svm_classifier, filename)

pipeline = PMMLPipeline([("model", SVC(C=best_C, kernel=best_kernel))])
pipeline.fit(X_train, y_train)
filename = "models/svm_model.pmml"
# sklearn2pmml(pipeline, filename, with_repr=True)
# print(pipeline.predict(X_valid))
# print(1)
