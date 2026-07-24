package com.example.data.remote

import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirestoreManager {

    private val db: FirebaseFirestore?
        get() = try {
            FirebaseApp.getInstance()
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.e("FirestoreManager", "Firestore instance error: ${e.message}")
            null
        }

    /**
     * Listens in real-time to changes in a Firestore collection.
     */
    fun listenToCollection(collectionName: String): Flow<List<Map<String, Any>>> = callbackFlow {
        val firestore = db
        if (firestore == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener: ListenerRegistration = firestore.collection(collectionName)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FirestoreManager", "Error observing $collectionName: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val dataList = snapshot.documents.mapNotNull { doc ->
                        doc.data?.plus("id" to doc.id)
                    }
                    trySend(dataList)
                }
            }

        awaitClose { listener.remove() }
    }

    /**
     * Upserts a document in a Firestore collection.
     */
    fun saveDocument(collectionName: String, documentId: String, data: Map<String, Any>) {
        db?.collection(collectionName)
            ?.document(documentId)
            ?.set(data, SetOptions.merge())
            ?.addOnSuccessListener {
                Log.d("FirestoreManager", "Successfully saved document $documentId in $collectionName")
            }
            ?.addOnFailureListener { e ->
                Log.e("FirestoreManager", "Error saving document $documentId in $collectionName: ${e.message}")
            }
    }

    /**
     * Deletes a document from a Firestore collection.
     */
    fun deleteDocument(collectionName: String, documentId: String) {
        db?.collection(collectionName)
            ?.document(documentId)
            ?.delete()
            ?.addOnSuccessListener {
                Log.d("FirestoreManager", "Successfully deleted document $documentId from $collectionName")
            }
            ?.addOnFailureListener { e ->
                Log.e("FirestoreManager", "Error deleting document $documentId from $collectionName: ${e.message}")
            }
    }
}
