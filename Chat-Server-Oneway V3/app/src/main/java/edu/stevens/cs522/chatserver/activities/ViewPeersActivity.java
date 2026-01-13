package edu.stevens.cs522.chatserver.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;

import java.util.List;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.databases.ChatDatabase;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;
import edu.stevens.cs522.chatserver.ui.SimpleArrayAdapter;


public class ViewPeersActivity extends FragmentActivity implements AdapterView.OnItemClickListener {

    /*
     * See ChatServer for example of what to do, query peers database instead of messages database.
     */

    private ChatDatabase chatDatabase;

    SimpleArrayAdapter<Peer> peersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {

            EdgeToEdge.enable(this);

            setContentView(R.layout.view_peers);

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.view_peers), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

        } else {

            setContentView(R.layout.view_peers);

        }

        peersAdapter = new SimpleArrayAdapter<>(this);
        ListView peersList = findViewById(R.id.peer_list);
        peersList.setAdapter(peersAdapter);

        chatDatabase = ChatDatabase.getInstance(getApplicationContext());

        /*
         * TODO query the database asynchronously, registering an observer for the result.
         */
        LiveData<List<Peer>> peers = chatDatabase.peerDao().fetchAllPeers();
        peers.observe(this, ms -> {
            peersAdapter.setElements(ms);
            peersAdapter.notifyDataSetChanged();
        });

        // TODO set item click listener to this activity
        peersList.setOnItemClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        chatDatabase = null;
    }

    /*
     * Callback interface defined in TextAdapter, for responding to clicks on rows.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*
         * Clicking on a peer brings up details
         */
        Peer peer = peersAdapter.getItem(position);

        Intent intent = new Intent(this, ViewPeerActivity.class);
        intent.putExtra(ViewPeerActivity.PEER_KEY, peer);
        startActivity(intent);

    }
}
