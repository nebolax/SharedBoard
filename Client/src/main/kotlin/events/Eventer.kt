package events

inline fun<reified Type: Event> register(noinline func: (event: Type) -> Unit) {
    val prefire: (event: Event) -> Unit = {
        if (it is Type) {
            func(it)
        }
    }
    listenersList.add(prefire)
}
fun emit(event: Event) {
    for (func in listenersList) {
        func(event)
    }
}
var listenersList = mutableListOf<(Event) -> Unit>()
open class Event {}