import express from 'express'
import mongoose from 'mongoose'
import {
	registerValidation,
	loginValidation,
	postCreateValidation,
} from './validations.js'
import checkAuth from './utils/checkAuth.js'
import * as UserController from './controllers/UserController.js'
import * as PostController from './controllers/PostController.js'

mongoose
	.connect(
		'mongodb+srv://banka:banka123@cluster.ovkftlo.mongodb.net/blog?retryWrites=true&w=majority&appName=Cluster'
	)
	.then(() => {
		console.log('DB started successfully!')
	})
	.catch(error => {
		console.log('DB error', error)
	})

const app = express()

app.use(express.json())

app.post('/auth/register', registerValidation, UserController.register)
app.post('/auth/login', loginValidation, UserController.login)
app.get('/auth/me', checkAuth, UserController.getMe)

// app.get('/posts', PostController.getAll)
// app.get('/posts/:id', PostController.getOne)
app.post('/posts', postCreateValidation, PostController.create)
// app.delete('/posts', PostController.remove)
// app.patch('/posts', PostController.update)

app.listen(5000, err => {
	if (err) {
		console.log(err)
	}
	console.log('Server started successfully!')
})
