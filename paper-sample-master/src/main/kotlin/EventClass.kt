import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class EventClass: Listener {

    @EventHandler
    fun PlayerJoinEvent(e: PlayerJoinEvent) {
        var player: Player = e.player

        if(player.hasPlayedBefore()) {
            e.joinMessage = "Hello!"
        } else if(!player.hasPlayedBefore()) {
            e.joinMessage = "Welcome!"
        }
    }

}