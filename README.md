# TiDB-Hack-Traveling-AI



## Inspiration
Our Trip Advisor application is an innovative, AI-driven travel planning tool designed to deliver personalized travel itineraries. The app integrates machine learning and weather data to optimize the user's travel experience by recommending suitable landmarks based on weather conditions and preferences.

## What it does
- Personalized Itinerary Generation: By providing their desired travel country, city, and estimated daily travel time, users are presented with a customized itinerary. 

- AI-Driven Recommendations: The application utilizes machine learning models trained on landmark attributes such as total occurrence, neighbor occurrence, and transportation time. This ensures the application provides the most suitable travel suggestions tailored to the user's inputs.

- Weather Integration: The app integrates with WeatherIO API to fetch real-time weather data, adjusting recommendations according to the weather conditions on each day of the planned itinerary.

- Intuitive User Interface: The user-friendly design of the application make users easily input their preferences and view their planned trip.

- Global Accessibility: The application has been deployed on Heroku, making it accessible from anywhere around the world. Users can plan their trips on-the-go, right from their web browsers.

## How we built it
- Backend Structure: We built the backbone of our application using Spring Boot due to its quick setup, embedded server, and production-ready features.

- Docker Containerization: To ensure smooth deployment and scalability, we used Docker to containerize our application. This provides the flexibility to manage services independently and ensures the application runs the same, regardless of the environment.

- Frontend: We constructed a dynamic, user-friendly frontend using a RESTful API approach that allowed us to create an intuitive and interactive user interface that communicates seamlessly with our backend services.

- Recommendation System: We developed a machine learning-driven recommendation system. The models are trained on several landmark attributes including total occurrence, neighbour occurrence, and transportation time, to generate personalized recommendations based on user preferences.

## Challenges we ran into
- Integrating Weather Data: The process of integrating real-time weather data into our recommendation system was a challenge. We had to ensure our model could adjust recommendations effectively based on real-time weather conditions.

- Building the Recommendation Model: Constructing a machine learning-driven model that could provide personalized travel suggestions based on diverse inputs was also a significant challenge. It required extensive data collection, preprocessing, and model training. We spent a lot of time verifying the feasibility of machine-learning algorithms.

## Accomplishments that we're proud of
- User-friendly Application: The user interface of our application is intuitive and interactive, and we've received positive feedback about its ease of use.

- Weather Integrated Recommendations: Despite the challenge, we successfully built a complete recommendation system which is the core of this application that can adjust travel suggestions based on real-time weather data and actively provide recommended travel itineraries based on user’s preferences.

## What we learned
- We learned how to build a robust backend structure using Spring Boot.

- We learned to build a recommendation system and tailor it to unique user inputs and preferences.

- We learned to tune different machine learning algorithms to achieve the best performance.

- We gained hands-on experience with Docker and deploying applications on cloud platforms like Heroku.

## What's next for Trip Advisor
- Better Data Source: Future work could include procuring more reliable and extensive data sources. The quality of recommendations provided by the system is inherently dependent on the quality of data available.

- Expanding Geographic Coverage: We can expand the number of cities and countries by the system. This would enable the service to cater to a larger user base with more diverse travel interests.

- Increasing Data Volume: As more data becomes available, the system could further refine its recommendations. This could include data from more users, more cities, more travel times, and more weather variations.

- Better-Tuned Machine Learning Models: As the system evolves, machine learning models could be continuously refined and tuned to improve prediction accuracy. This could involve using more advanced models, hyperparameter tuning, ensemble methods, or better feature engineering.

- Personalized Recommendations: The system could potentially evolve to include more personalized recommendations based on user preferences. This could involve gathering data about user preferences, past trips, likes/dislikes, etc., and incorporating these into the recommendation model.



## URL to access
https://tidb-88afe2041bef.herokuapp.com/
