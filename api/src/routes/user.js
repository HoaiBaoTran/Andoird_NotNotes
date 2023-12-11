const express = require('express')
const router = express.Router()
const userController = require('../app/controllers/UserController')

router.post('/', userController.addUser)
router.get('/', userController.getAllUsers)
router.get('/:id', userController.getUserById)
router.post('/email', userController.getUserByEmail)
router.put('/:id', userController.updateUserById)
router.put('/password/:id', userController.updateUserPasswordById)

module.exports = router