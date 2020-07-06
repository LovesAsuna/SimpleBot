package me.lovesasuna.bot.util.exceptions

class AccountNotFoundException : Exception {
    constructor(cause: Throwable) : super(cause)
    constructor(message: String) : super(message)
}