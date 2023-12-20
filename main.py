# Library API
from flask import Flask, request, jsonify
import os

# Library Dataset 
import pandas as pd

# Library Data Preprocessing
import numpy as  np

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
model.fit(ratings.batch(4096), epochs=10)

# Brute-Force Search Index
index = tfrs.layers.factorized_top_k.BruteForce(model.user_model)
index.index_from_dataset(food.batch(100).map(lambda food_name: (food_name, model.food_model(food_name))))

# Flask Application
@app.route('/recommendation', methods=['POST'])
def recommendation():
    user_id = request.json['user_id']
    _, food_name = index(np.array([user_id]))
    recommended_food_names = [name.decode('UTF-8') for name in food_name[0, :4].numpy()]
    return jsonify({"message": "Predict success!", "status": 200, "data": recommended_food_names})

@app.route('/search', methods=['POST'])
def search():
    search = request.json['food_name']
    search = df_food[df_food['food_name'].str.contains(search)]
    srjs = search.to_dict('records')

    return {"message": "Search success deck!", "status": 200, "data": srjs}

@app.route('/')
def ok():
    return jsonify({"message": "WELCOME TO RECIPE API"})

if __name__ == '__main__':
    app.run(host="127.0.0.10", debug=True, port=int(os.getenv("PORT", 5000)))
