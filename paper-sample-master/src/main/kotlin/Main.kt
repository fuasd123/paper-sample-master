import com.github.noonmaru.kommand.argument.double
import com.github.noonmaru.kommand.argument.integer
import com.github.noonmaru.kommand.argument.player
import com.github.noonmaru.kommand.kommand
import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.permission.Permission
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

class Main: JavaPlugin(), Listener {

    private lateinit var log: Logger
    private lateinit var econ: Economy
    private lateinit var perm: Permission
    private lateinit var chat: Chat


    override fun onEnable() {
        log.info("msg")
        server.pluginManager.registerEvents(EventClass() , this)
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupPermissions();
        setupChat();
        setUpCommands()
    }
    private fun setupEconomy(): Boolean {
        if (server.pluginManager.getPlugin("Vault") == null) {
            return false
        }
        val rsp = server.servicesManager.getRegistration(Economy::class.java)
            ?: return false
        econ = rsp.provider
        return true
    }

    private fun setupChat(): Boolean {
        val rsp = server.servicesManager.getRegistration(Chat::class.java)
        chat = rsp!!.getProvider()
        return true
    }

    private fun setupPermissions(): Boolean {
        val rsp = server.servicesManager.getRegistration(
            Permission::class.java
        )
        perm = rsp!!.getProvider()
        return true
    }

    override fun onDisable() {
        super.onDisable()
        log.info("msg")
    }

    private fun testFunction(player: Player) {
        log.info("Test!")
        player.sendMessage("Test!")
    }

    fun sendMoney(target: Player , sender: Player ,money: Double) {
        val deposit = econ.depositPlayer(target , money)
        val withdraw = econ.withdrawPlayer(sender , money)
        if(deposit.transactionSuccess() && withdraw.transactionSuccess()) {
            target.sendMessage("${sender.name}님이 $money 원을 보냈왔습니다!")
            sender.sendMessage("${target.name}님에게 $money 원을 보냈습니다")
        } else if(!deposit.transactionSuccess() || !withdraw.transactionSuccess()) {
            sender.sendMessage("알수없는 이유로 실패하였습니다!")
        }
    }

    private fun setUpCommands() {
        kommand {
            register("test") {
                require {
                    this is Player
                }
                executes {
                    val player: Player = it.sender as Player
                    testFunction(player)
                }
            }
            register("sendmoney") {
                then("target" to player()) {
                    then("value" to double()) {
                        executes {
                            val value = it.parseArgument<Double>("value")
                            val target = it.parseArgument<Player>("target")
                            val sender = it.sender as Player

                            sendMoney(target,sender , value)
                        }
                    }
                }
            }
        }
    }
}