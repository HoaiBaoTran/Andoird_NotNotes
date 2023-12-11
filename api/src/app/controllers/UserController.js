const db = require('../database/connection')

class UserController {
    index (req, res, next) {
        res.json({
            code: 0,
            message: 'User route'
        })
    }

    getAllUsers (req, res, next) {
        db.connect(function(err) {
            if (err) throw err;
            //Select all customers and return the result object:
            db.query("SELECT * FROM users", function (err, result, fields) {
                if (err) throw err;
                res.json({
                        code: 200,
                        message: 'Get all users success',
                        data: result
                    });
            });
          });
    }

    getUserById (req, res, next) {
        db.connect(function(err) {
            if (err) throw err;
            //Select all customers and return the result object:
            const sql = 'SELECT * FROM users WHERE id = ?'
            const id = req.params.id
            db.query(sql, id, function (err, result, fields) {
                if (err) {
                    throw(err)
                }
                else if (result.length === 0) {
                    res.json({
                        code: 404,
                        message: 'User not found'
                    })
                }
                else {
                    res.json({
                            code: 200,
                            message: 'Get user success',
                            data: result
                    });
                }
            });
          });
    }

    getUserByEmail (req, res, next) {
        db.connect(function (err) {
            if (err) throw err;
            const sql = 'SELECT * FROM users WHERE email = ?'
            const {email} = req.body
            db.query(sql, email, function (err, result, fields) {
                if (err) {
                    throw(err)
                }
                else if (result.length === 0) {
                    res.json({
                        code: 404,
                        message: 'User not found'
                    })
                }
                else {
                    res.json({
                            code: 200,
                            message: 'Get user success',
                            data: result
                    });
                }
            })
        })
    }

    addUser (req, res, next) {
        db.connect (function (err) {
            if (err) {
                throw err
            }
            const sql = "INSERT INTO users(`name`, `email`, `password`) VALUES (?, ?, ?)"
            const {name, email, password} = req.body
            const params = [name, email, password]
            db.query(sql, params, function (err, result, fields) {
                if (err) {
                    throw err
                }
                res.json ({
                    code: 200,
                    message: 'Add user successfully',
                    data: result
                })
            })
        })
    }

    updateUserById (req, res, next) {
        db.connect (function (err) {
            if (err)
                throw err;
            const {email, name, phoneNumber, address, job, homepage} = req.body
            const sql = `UPDATE users SET name = ?, email = ?, phoneNumber = ?, address = ?, job = ?, homepage = ? WHERE id = ?`
            const id = req.params.id
            const params = [name, email, phoneNumber, address, job, homepage, id]
            db.query(sql, params, function (err, result, fields) {
                if (err) {
                    res.json({
                        code: 404,
                        message: 'Update user failed',
                        err
                    })
                }
                else {
                    res.json({
                        code: 200,
                        message: 'Update user success',
                        data: result
                    });
                }
            })
        })
    
    }

    updateUserPasswordById(req, res, next) {
        db.connect (function (err) {
            if (err)
                throw err;
            const {password} = req.body
            const sql = `UPDATE users SET password = ? WHERE id = ?`
            const id = req.params.id
            const params = [password, id]
            db.query(sql, params, function (err, result, fields) {
                if (err) {
                    res.json({
                        code: 404,
                        message: 'Update password failed',
                        err
                    })
                }
                else {
                    res.json({
                        code: 200,
                        message: 'Update password success',
                        data: result
                    });
                }
            })
        })
    } 
}

module.exports = new UserController