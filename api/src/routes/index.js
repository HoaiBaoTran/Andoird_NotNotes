const userRouter = require('./user.js')

function route(app) {
    app.use('/api/users', userRouter)
}

module.exports = route