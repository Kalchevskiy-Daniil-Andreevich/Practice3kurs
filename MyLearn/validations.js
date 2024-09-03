import { body } from 'express-validator'

export const loginValidation = [
	body('email', 'Неверный формат почты').isEmail(),
	body('password', 'Неверный пароль').isLength({ min: 5 }),
]

export const registerValidation = [
	body('email', 'Неверный формат почты').isEmail(),
	body('password', 'Неверный пароль').isLength({ min: 5 }),
	body('fullName', 'Укажите верное имя').isLength({ min: 3 }),
	body('avatarUrl', 'Укажите ссылку на аватар').optional().isURL(),
]

export const postCreateValidation = [
	body('title', 'Введите заголовок статьи').isLength({ min: 3 }),
	body('text', 'Введите текст статьи').isLength({ min: 10 }),
	body('tags', 'Неверный формат тэгов').optional().isString(),
	body('imageUrl', 'Неверно указана картинка').optional().isString(),
]
