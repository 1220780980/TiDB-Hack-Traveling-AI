from pypmml import Model

model = Model.fromFile("models/decision_tree_model.pmml")
result = model.predict({
    "total_occurrence" : 0.8,
    "neighbour_occurrence" : 0.7,
    "transportation_time" : 10.0
})
print(result)

model = Model.fromFile("models/knn_model.pmml")
result = model.predict({
    "total_occurrence" : 0.8,
    "neighbour_occurrence" : 0.7,
    "transportation_time" : 10.0
})
print(result)

model = Model.fromFile("models/logistic_regression_model.pmml")
result = model.predict({
    "total_occurrence" : 0.8,
    "neighbour_occurrence" : 0.7,
    "transportation_time" : 10.0
})
print(result)

model = Model.fromFile("models/random_forest_model.pmml")
result = model.predict({
    "total_occurrence" : 0.8,
    "neighbour_occurrence" : 0.7,
    "transportation_time" : 10.0
})
print(result)

model = Model.fromFile("models/svm_model.pmml")
result = model.predict({
    "total_occurrence" : 0.8,
    "neighbour_occurrence" : 0.7,
    "transportation_time" : 10.0
})
print(result)

