import PostModel from '../models/post.js'

export const create = async (req, res) => {
	try {
		const document = new PostModel({
			title: req.body.title,
			text: req.body.text,
			tags: req.body.tags,
			imageUrl: req.body.imageUrl,
			user: req.userId,
		})

		const post = await document.save()

		res.json(post)
	} catch (err) {
		console.log(err)
		res.status(500).json({
			success: false,
			message: 'Не удалось создать статью',
		})
	}
}
