# Library API
from flask import Flask, request, jsonify
import os

# Library Dataset 
import pandas as pd

# Library Data Preprocessing
import numpy as  np
import random

# Library Model
import tensorflow as tf
from tensorflow import keras
from sklearn.preprocessing import StandardScaler, MinMaxScaler
from sklearn.model_selection import train_test_split
from sklearn.metrics.pairwise import cosine_similarity
from sklearn.metrics.pairwise import euclidean_distances
from tensorflow.keras.models import Sequential, Model
from tensorflow.keras.layers import Dense, Input
from tensorflow.keras.optimizers import Adam
from typing import Dict, Text

import numpy as np
import tensorflow as tf
import pandas as pd
import tensorflow_recommenders as tfrs

app = Flask(__name__)

# Load the datasets
df_interactions = pd.read_csv('dataset/Interaksi_Pengguna_dengan_Resep.csv')
df_food = pd.read_csv('dataset/Resep_Dengan_Kolom_Alergen.csv')

# Ensure correct data types
df_interactions['user_id'] = df_interactions['user_id'].astype(str)
df_interactions['food_name'] = df_interactions['food_name'].astype(str)
df_food['food_name'] = df_food['food_name'].astype(str)

# TensorFlow Dataset Creation
def create_tf_dataset(dataframe, is_food=False):
    if is_food:
        return tf.data.Dataset.from_tensor_slices(
            {'food_name': tf.constant(dataframe['food_name'].values, dtype=tf.string)}
        )
    else:
        return tf.data.Dataset.from_tensor_slices({
            'user_id': tf.constant(dataframe['user_id'].values, dtype=tf.string),
            'food_name': tf.constant(dataframe['food_name'].values, dtype=tf.string),
            'rating': tf.constant(dataframe['rating'].values, dtype=tf.int32),
        })

ratings = create_tf_dataset(df_interactions)
food = create_tf_dataset(df_food, is_food=True).map(lambda x: x["food_name"])

# Vocabulary
user_ids_vocabulary = tf.keras.layers.StringLookup(mask_token=None)
user_ids_vocabulary.adapt(ratings.map(lambda x: x["user_id"]))
food_name_vocabulary = tf.keras.layers.StringLookup(mask_token=None)
food_name_vocabulary.adapt(food)

# Model Definition
class FoodLensModel(tfrs.Model):
  # We derive from a custom base class to help reduce boilerplate. Under the hood,
  # these are still plain Keras Models.

  def __init__(
      self,
      user_model: tf.keras.Model,
      food_model: tf.keras.Model,
      task: tfrs.tasks.Retrieval):
    super().__init__()

    # Set up user and food representations.
    self.user_model = user_model
    self.food_model = food_model

    # Set up a retrieval task.
    self.task = task

  def compute_loss(self, features: Dict[Text, tf.Tensor], training=False) -> tf.Tensor:
    # Define how the loss is computed.

    user_embeddings = self.user_model(features["user_id"])
    food_embeddings = self.food_model(features["food_name"])

    return self.task(user_embeddings, food_embeddings)

# Model Building
# Define user and movie models.
user_model = tf.keras.Sequential([
    user_ids_vocabulary,
    tf.keras.layers.Embedding(user_ids_vocabulary.vocab_size(), 64)
])
food_model = tf.keras.Sequential([
    food_name_vocabulary,
    tf.keras.layers.Embedding(food_name_vocabulary.vocab_size(), 64)
])

# Define your objectives.
task = tfrs.tasks.Retrieval(metrics=tfrs.metrics.FactorizedTopK(
    food.batch(128).map(food_model)
  )
)
model = FoodLensModel(user_model, food_model, task)
model.compile(optimizer=tf.keras.optimizers.Adagrad(0.5))

# Train the model
model.fit(ratings.batch(4096), epochs=50)

# Brute-Force Search Index
index = tfrs.layers.factorized_top_k.BruteForce(model.user_model)
index.index_from_dataset(food.batch(100).map(lambda food_name: (food_name, model.food_model(food_name))))

#============================ Batas Recommenders ==============================================

#Food diet recommendation
dataset=df_food.copy()
columns=['name', 'food_name', 'Images',
         'CookTime', 'PrepTime', 'TotalTime',
         'RecipeIngredientParts', 'Calories', 'FatContent',
         'CarbohydrateContent', 'ProteinContent', 'RecipeServings',
         'RecipeInstructions','is_seafood','is_nut', 'is_lactose'
         ]
dataset=dataset[columns]
dataset.head()

extracted_data=dataset.copy()
extracted_data = extracted_data[extracted_data.Images != 'character(0)']
extracted_data.to_csv('Resep_Dengan_Kolom_Alergen.csv', index=False)

from sklearn.metrics.pairwise import euclidean_distances

# Define the recommendation function
def get_recommendations(input_calories, input_seafood, input_nut, input_lactose, top_k):
    # Calculate the Euclidean distances between input calories and all recipes
    distances = euclidean_distances(extracted_data[['Calories']], [[input_calories]])

    # Add distances as a new column in the dataset
    extracted_data['Distance'] = distances

    if input_seafood == 1:
      filtered_recipes = extracted_data[
        (extracted_data['is_seafood'] == 0)
      ]

    if input_nut == 1:
      filtered_recipes = extracted_data[
        (extracted_data['is_nut'] == 0)
      ]

    if input_lactose == 1:
      filtered_recipes = extracted_data[
        (extracted_data['is_lactose'] == 0)
      ]
    # Sort recipes based on the distances in ascending order
    sorted_recipes = filtered_recipes.sort_values('Distance')

    # Select top k recipes
    top_recipes = sorted_recipes.head(top_k)

    return top_recipes


# Flask Application
@app.route('/recommendation', methods=['POST'])
def recommendation():
    user_id = request.json['user_id']
    _, food_name = index(np.array([user_id]))
    recommended_food_names = [name.decode('UTF-8') for name in food_name[0, :4].numpy()]
    return jsonify({"message": "Predict success!", "status": 200, "data": recommended_food_names})

@app.route('/search', methods=['GET'])
def search_get():
    args = request.args
    search = args.get('food_name')
    search = df_food[df_food['food_name'].str.contains(search)]
    srjs = search.to_dict('records')

    return {"message": "Search success deck!", "status": 200, "data": srjs}

@app.route('/food-recomendation', methods=['POST'])
def food():
    calories = request.json['calories']
    input_seafood = request.json['input_seafood']
    input_nut = request.json['input_nut']
    input_lactose = request.json['input_lactose']

    #Count calories divided by 6 meals
    count_calories_per_meal = calories / 3

    breakfast_1 = random.uniform(10,count_calories_per_meal)
    recommended_recipes_breakfast_1 = get_recommendations(breakfast_1, input_seafood, input_nut, input_lactose, top_k=1)
    breakfast_2 = count_calories_per_meal - breakfast_1
    recommended_recipes_breakfast_2 = get_recommendations(breakfast_2, input_seafood, input_nut, input_lactose, top_k=1)

    lunch_1 = random.uniform(10,count_calories_per_meal)
    recommended_recipes_lunch_1 = get_recommendations(lunch_1, input_seafood, input_nut, input_lactose, top_k=1)
    lunch_2 = count_calories_per_meal - lunch_1
    recommended_recipes_lunch_2 = get_recommendations(lunch_2, input_seafood, input_nut, input_lactose, top_k=1)


    dinner_1 = random.uniform(10,count_calories_per_meal)
    recommended_recipes_dinner_1 = get_recommendations(dinner_1, input_seafood, input_nut, input_lactose, top_k=1)
    dinner_2 = count_calories_per_meal - dinner_1
    recommended_recipes_dinner_2 = get_recommendations(dinner_2, input_seafood, input_nut, input_lactose, top_k=1)


    recommended_recipes = get_recommendations(calories, input_seafood, input_nut, input_lactose, top_k=10)
    recommended_recipes_breakfast_1 = recommended_recipes_breakfast_1.to_dict('records')
    recommended_recipes_breakfast_2 = recommended_recipes_breakfast_2.to_dict('records')
    recommended_recipes_lunch_1 = recommended_recipes_lunch_1.to_dict('records')
    recommended_recipes_lunch_2 = recommended_recipes_lunch_2.to_dict('records')
    recommended_recipes_dinner_1 = recommended_recipes_dinner_1.to_dict('records')
    recommended_recipes_dinner_2 = recommended_recipes_dinner_2.to_dict('records')


    srjs = recommended_recipes.to_dict('records')
    return  {   
                "message": "Recomendation success deck!", 
                "status": 200, 
                "breakfast_1": recommended_recipes_breakfast_1,
                "breakfast_2": recommended_recipes_breakfast_2,
                "lunch_1": recommended_recipes_lunch_1,
                "lunch_2": recommended_recipes_lunch_2,
                "dinner_1": recommended_recipes_dinner_1,
                "dinner_2": recommended_recipes_dinner_2,
                "totral_calories": calories,
            }


@app.route('/food-recommendation-get', methods=['GET'])
def food_get():
    args = request.args
    calories = args.get("calories", default=0, type=float)
    input_seafood = args.get('input_seafood', type=int)
    input_nut = args.get('input_nut', type=int)
    input_lactose = args.get('input_lactose', type=int)

    #Count calories divided by 6 meals
    count_calories_per_meal = calories / 3

    breakfast_1 = random.uniform(10,count_calories_per_meal)
    recommended_recipes_breakfast_1 = get_recommendations(breakfast_1, input_seafood, input_nut, input_lactose, top_k=1)
    breakfast_2 = count_calories_per_meal - breakfast_1
    recommended_recipes_breakfast_2 = get_recommendations(breakfast_2, input_seafood, input_nut, input_lactose, top_k=1)

    lunch_1 = random.uniform(10,count_calories_per_meal)
    recommended_recipes_lunch_1 = get_recommendations(lunch_1, input_seafood, input_nut, input_lactose, top_k=1)
    lunch_2 = count_calories_per_meal - lunch_1
    recommended_recipes_lunch_2 = get_recommendations(lunch_2, input_seafood, input_nut, input_lactose, top_k=1)


    dinner_1 = random.uniform(10,count_calories_per_meal)
    recommended_recipes_dinner_1 = get_recommendations(dinner_1, input_seafood, input_nut, input_lactose, top_k=1)
    dinner_2 = count_calories_per_meal - dinner_1
    recommended_recipes_dinner_2 = get_recommendations(dinner_2, input_seafood, input_nut, input_lactose, top_k=1)


    recommended_recipes = get_recommendations(calories, input_seafood, input_nut, input_lactose, top_k=10)
    recommended_recipes_breakfast_1 = recommended_recipes_breakfast_1.to_dict('records')
    recommended_recipes_breakfast_2 = recommended_recipes_breakfast_2.to_dict('records')
    recommended_recipes_lunch_1 = recommended_recipes_lunch_1.to_dict('records')
    recommended_recipes_lunch_2 = recommended_recipes_lunch_2.to_dict('records')
    recommended_recipes_dinner_1 = recommended_recipes_dinner_1.to_dict('records')
    recommended_recipes_dinner_2 = recommended_recipes_dinner_2.to_dict('records')


    srjs = recommended_recipes.to_dict('records')
    return  {   
                "message": "Recomendation success deck!", 
                "status": 200, 
                "breakfast_1": recommended_recipes_breakfast_1,
                "breakfast_2": recommended_recipes_breakfast_2,
                "lunch_1": recommended_recipes_lunch_1,
                "lunch_2": recommended_recipes_lunch_2,
                "dinner_1": recommended_recipes_dinner_1,
                "dinner_2": recommended_recipes_dinner_2,
                "totral_calories": calories,
            }

# Flask Application
@app.route('/recommendation-get', methods=['GET'])
def recommendation_get():
    args = request.args
    user_id = args.get('user_id')
    _, food_name = index(np.array([user_id]))
    recommended_food_names = [name.decode('UTF-8') for name in food_name[0, :4].numpy()]
    return jsonify({"message": "Predict success!", "status": 200, "data": recommended_food_names})

@app.route('/')
def ok():
    return jsonify({"message": "WELCOME TO RECIPE API"})

if __name__ == '__main__':
    app.run(host="127.0.0.10", debug=True, port=int(os.getenv("PORT", 5000)))
